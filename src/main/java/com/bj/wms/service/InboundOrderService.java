package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.InboundOrder;
import com.bj.wms.entity.InboundOrderItem;
import com.bj.wms.entity.InboundStatus;
import com.bj.wms.mapper.InboundOrderMapper;
import com.bj.wms.repository.InboundOrderRepository;
import com.bj.wms.repository.InboundOrderItemRepository;
import com.bj.wms.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboundOrderService {

    private final InboundOrderRepository orderRepository;
    private final InboundOrderItemRepository itemRepository;

    /**
     * 分页查询入库单列表
     */
    public PageResult<InboundOrderDTO> getOrderList(InboundOrderQueryRequest request) {
        Pageable pageable = PageRequest.of(
            request.getPage() - 1, 
            request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Specification<InboundOrder> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(request.getOrderNo())) {
                predicates.add(cb.like(root.get("orderNo"), "%" + request.getOrderNo() + "%"));
            }
            if (request.getWarehouseId() != null) {
                predicates.add(cb.equal(root.get("warehouseId"), request.getWarehouseId()));
            }
            if (request.getSupplierId() != null) {
                predicates.add(cb.equal(root.get("supplierId"), request.getSupplierId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), InboundStatus.fromCode(request.getStatus())));
            }
            if (StringUtils.hasText(request.getStartTime())) {
                try {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), LocalDate.parse(request.getStartTime()).atStartOfDay()));
                } catch (Exception e) {
                    log.warn("Invalid startTime format: {}", request.getStartTime());
                }
            }
            if (StringUtils.hasText(request.getEndTime())) {
                try {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), LocalDate.parse(request.getEndTime()).atTime(23, 59, 59)));
                } catch (Exception e) {
                    log.warn("Invalid endTime format: {}", request.getEndTime());
                }
            }
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<InboundOrder> page = orderRepository.findAll(spec, pageable);
        
        List<InboundOrderDTO> dtoList = page.getContent().stream()
            .map(InboundOrderMapper::toDTO)
            .toList();

        return new PageResult<>(dtoList, page.getTotalElements());
    }

    /**
     * 获取入库单详情
     */
    public InboundOrderDTO getOrderDetail(Long id) {
        InboundOrder order = orderRepository.findByIdWithItems(id)
            .orElseThrow(() -> new RuntimeException("入库单不存在"));
        return InboundOrderMapper.toDTO(order, true);
    }

    /**
     * 创建入库单
     */
    @Transactional
    public InboundOrderDTO createOrder(InboundOrderCreateRequest request) {
        // 生成入库单号
        String orderNo = generateOrderNo();
        
        InboundOrder order = InboundOrderMapper.toEntity(request);
        order.setOrderNo(orderNo);
        
        order = orderRepository.save(order);
        
        log.info("创建入库单成功: {}", orderNo);
        return InboundOrderMapper.toDTO(order, true);
    }

    /**
     * 更新入库单
     */
    @Transactional
    public InboundOrderDTO updateOrder(Long id, InboundOrderCreateRequest request) {
        InboundOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("入库单不存在"));
        
        if (order.getStatus() != InboundStatus.PENDING) {
            throw new RuntimeException("只有待收货状态的入库单才能修改");
        }
        
        // 删除原有明细
        itemRepository.deleteByInboundOrderId(id);
        
        // 更新基本信息
        order.setWarehouseId(request.getWarehouseId());
        order.setSupplierId(request.getSupplierId());
        
        // 重新创建明细
        if (request.getItems() != null) {
            final InboundOrder finalOrder = order; // 创建 effectively final 变量
            List<InboundOrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    InboundOrderItem item = new InboundOrderItem();
                    item.setInboundOrderId(id);
                    item.setProductSkuId(itemRequest.getProductSkuId());
                    item.setExpectedQuantity(itemRequest.getExpectedQuantity());
                    item.setUnitPrice(itemRequest.getUnitPrice());
                    item.setBatchNo(itemRequest.getBatchNo());
                    item.setProductionDate(itemRequest.getProductionDate());
                    item.setExpiryDate(itemRequest.getExpiryDate());
                    item.setInboundOrder(finalOrder);
                    return item;
                })
                .toList();
            
            itemRepository.saveAll(items);
            order.setOrderItems(items);
            
            // 重新计算总预期数量
            int totalExpected = items.stream()
                .mapToInt(InboundOrderItem::getExpectedQuantity)
                .sum();
            order.setTotalExpectedQuantity(totalExpected);
        }
        
        order = orderRepository.save(order);
        
        log.info("更新入库单成功: {}", order.getOrderNo());
        return InboundOrderMapper.toDTO(order, true);
    }

    /**
     * 删除入库单
     */
    @Transactional
    public void deleteOrder(Long id) {
        InboundOrder order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("入库单不存在"));
        
        if (order.getStatus() != InboundStatus.PENDING) {
            throw new RuntimeException("只有待收货状态的入库单才能删除");
        }
        
        orderRepository.deleteById(id);
        log.info("删除入库单成功: {}", order.getOrderNo());
    }

    /**
     * 确认收货
     */
    @Transactional
    public void confirmReceipt(Long orderId, List<ConfirmReceiptRequest.ReceiptItemRequest> receiptItems) {
        InboundOrder order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new RuntimeException("入库单不存在"));
        
        if (order.getStatus() == InboundStatus.COMPLETED) {
            throw new RuntimeException("入库单已完成，不能重复确认收货");
        }
        
        if (order.getStatus() == InboundStatus.CANCELED) {
            throw new RuntimeException("入库单已取消，不能确认收货");
        }
        
        // 更新明细的收货数量
        for (ConfirmReceiptRequest.ReceiptItemRequest receiptItem : receiptItems) {
            InboundOrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProductSkuId().equals(receiptItem.getProductSkuId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到对应的入库明细"));
            
            orderItem.setReceivedQuantity(receiptItem.getReceivedQuantity());
            itemRepository.save(orderItem);
        }
        
        // 重新计算总收货数量
        int totalReceived = order.getOrderItems().stream()
            .mapToInt(InboundOrderItem::getReceivedQuantity)
            .sum();
        order.setTotalReceivedQuantity(totalReceived);
        
        // 更新状态
        if (totalReceived >= order.getTotalExpectedQuantity()) {
            order.setStatus(InboundStatus.COMPLETED);
        } else if (totalReceived > 0) {
            order.setStatus(InboundStatus.PARTIAL);
        }
        
        orderRepository.save(order);
        
        log.info("确认收货成功: {}, 收货数量: {}/{}", order.getOrderNo(), totalReceived, order.getTotalExpectedQuantity());
    }

    /**
     * 生成入库单号
     */
    private String generateOrderNo() {
        String dateStr = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "IN" + dateStr + uuid;
    }
}
