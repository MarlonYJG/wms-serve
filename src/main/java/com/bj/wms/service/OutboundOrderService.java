package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.*;
import com.bj.wms.mapper.OutboundOrderMapper;
import com.bj.wms.mapper.PickingTaskMapper;
import com.bj.wms.repository.*;
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

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 出库单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboundOrderService {

    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderItemRepository outboundOrderItemRepository;
    private final PickingTaskRepository pickingTaskRepository;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final ProductSkuRepository productSkuRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * 分页查询出库单列表
     */
    public PageResult<OutboundOrderDTO> getOrderList(OutboundOrderQueryRequest request) {
        // 构建查询条件
        Specification<OutboundOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getOrderNo() != null && !request.getOrderNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("orderNo"), "%" + request.getOrderNo() + "%"));
            }
            if (request.getWarehouseId() != null) {
                predicates.add(cb.equal(root.get("warehouseId"), request.getWarehouseId()));
            }
            if (request.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), request.getCustomerId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getStartTime() != null && !request.getStartTime().trim().isEmpty()) {
                LocalDateTime startTime = LocalDateTime.parse(request.getStartTime() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), startTime));
            }
            if (request.getEndTime() != null && !request.getEndTime().trim().isEmpty()) {
                LocalDateTime endTime = LocalDateTime.parse(request.getEndTime() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), endTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        Pageable pageable = PageRequest.of(
            request.getPage() - 1, 
            request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Page<OutboundOrder> page = outboundOrderRepository.findAll(spec, pageable);

        // 转换为DTO
        List<OutboundOrderDTO> content = page.getContent().stream()
            .map(OutboundOrderMapper::toDTO)
            .collect(Collectors.toList());

        return new PageResult<>(content, page.getNumber() + 1, page.getSize(), page.getTotalElements());
    }

    /**
     * 获取出库单详情
     */
    public OutboundOrderDTO getOrderDetail(Long id) {
        OutboundOrder order = outboundOrderRepository.findByIdWithItems(id)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));
        return OutboundOrderMapper.toDTO(order);
    }

    /**
     * 创建出库单
     */
    @Transactional
    public OutboundOrderDTO createOrder(OutboundOrderCreateRequest request) {
        // 验证仓库和客户是否存在
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new RuntimeException("仓库不存在"));
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new RuntimeException("客户不存在"));

        // 生成出库单号
        String orderNo = generateOrderNo();

        // 创建出库单
        OutboundOrder order = new OutboundOrder();
        order.setOrderNo(orderNo);
        order.setWarehouseId(request.getWarehouseId());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(1); // 待处理
        order.setCustomerInfo(request.getCustomerInfo());
        order.setCreatedTime(LocalDateTime.now());

        order = outboundOrderRepository.save(order);

        // 创建出库单明细
        List<OutboundOrderItem> items = new ArrayList<>();
        for (OutboundOrderCreateRequest.OutboundOrderItemCreateRequest itemRequest : request.getItems()) {
            // 验证商品SKU是否存在
            ProductSku productSku = productSkuRepository.findById(itemRequest.getProductSkuId())
                .orElseThrow(() -> new RuntimeException("商品SKU不存在: " + itemRequest.getProductSkuId()));

            OutboundOrderItem item = new OutboundOrderItem();
            item.setOutboundOrderId(order.getId());
            item.setProductSkuId(itemRequest.getProductSkuId());
            item.setQuantity(itemRequest.getQuantity());
            item.setAllocatedQuantity(0);
            item.setPickedQuantity(0);
            item.setCreatedTime(LocalDateTime.now());

            items.add(item);
        }

        outboundOrderItemRepository.saveAll(items);
        order.setItems(items);

        return OutboundOrderMapper.toDTO(order);
    }

    /**
     * 更新出库单
     */
    @Transactional
    public OutboundOrderDTO updateOrder(Long id, OutboundOrderCreateRequest request) {
        OutboundOrder order = outboundOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        // 只有待处理状态的出库单才能修改
        if (order.getStatus() != 1) {
            throw new RuntimeException("只有待处理状态的出库单才能修改");
        }

        // 验证仓库和客户是否存在
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
            .orElseThrow(() -> new RuntimeException("仓库不存在"));
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new RuntimeException("客户不存在"));

        // 更新基本信息
        order.setWarehouseId(request.getWarehouseId());
        order.setCustomerId(request.getCustomerId());
        order.setCustomerInfo(request.getCustomerInfo());
        order.setUpdatedTime(LocalDateTime.now());

        // 删除原有明细
        outboundOrderItemRepository.deleteByOutboundOrderId(id);

        // 创建新明细
        List<OutboundOrderItem> items = new ArrayList<>();
        for (OutboundOrderCreateRequest.OutboundOrderItemCreateRequest itemRequest : request.getItems()) {
            // 验证商品SKU是否存在
            ProductSku productSku = productSkuRepository.findById(itemRequest.getProductSkuId())
                .orElseThrow(() -> new RuntimeException("商品SKU不存在: " + itemRequest.getProductSkuId()));

            OutboundOrderItem item = new OutboundOrderItem();
            item.setOutboundOrderId(order.getId());
            item.setProductSkuId(itemRequest.getProductSkuId());
            item.setQuantity(itemRequest.getQuantity());
            item.setAllocatedQuantity(0);
            item.setPickedQuantity(0);
            item.setCreatedTime(LocalDateTime.now());

            items.add(item);
        }

        outboundOrderItemRepository.saveAll(items);
        order.setItems(items);

        order = outboundOrderRepository.save(order);
        return OutboundOrderMapper.toDTO(order);
    }

    /**
     * 删除出库单
     */
    @Transactional
    public void deleteOrder(Long id) {
        OutboundOrder order = outboundOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        // 只有待处理状态的出库单才能删除
        if (order.getStatus() != 1) {
            throw new RuntimeException("只有待处理状态的出库单才能删除");
        }

        // 删除明细
        outboundOrderItemRepository.deleteByOutboundOrderId(id);
        
        // 删除出库单
        outboundOrderRepository.deleteById(id);
    }

    /**
     * 分配库存
     */
    @Transactional
    public void allocateInventory(Long id) {
        OutboundOrder order = outboundOrderRepository.findByIdWithItems(id)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        // 只有待处理状态的出库单才能分配库存
        if (order.getStatus() != 1) {
            throw new RuntimeException("只有待处理状态的出库单才能分配库存");
        }

        // 检查库存并分配
        for (OutboundOrderItem item : order.getItems()) {
            // 查询可用库存
            List<Inventory> inventories = inventoryRepository.findByWarehouseIdAndProductSkuIdAndQuantityGreaterThan(
                order.getWarehouseId(), item.getProductSkuId(), 0);

            int totalAvailable = inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

            if (totalAvailable < item.getQuantity()) {
                throw new RuntimeException("库存不足，商品SKU: " + item.getProductSkuId() + 
                    "，需要: " + item.getQuantity() + "，可用: " + totalAvailable);
            }

            // 分配库存（简单按FIFO分配）
            int remainingQuantity = item.getQuantity();
            for (Inventory inventory : inventories) {
                if (remainingQuantity <= 0) break;

                int allocateQuantity = Math.min(remainingQuantity, inventory.getQuantity());
                inventory.setLockedQuantity(inventory.getLockedQuantity() + allocateQuantity);
                inventory.setUpdatedTime(LocalDateTime.now());
                inventoryRepository.save(inventory);

                remainingQuantity -= allocateQuantity;
            }

            // 更新已分配数量
            item.setAllocatedQuantity(item.getQuantity());
            item.setUpdatedTime(LocalDateTime.now());
        }

        // 更新出库单状态
        order.setStatus(2); // 已分配库存
        order.setUpdatedTime(LocalDateTime.now());
        outboundOrderRepository.save(order);

        // 保存明细
        outboundOrderItemRepository.saveAll(order.getItems());
    }

    /**
     * 生成拣货任务
     */
    @Transactional
    public void generatePickingTasks(Long id) {
        OutboundOrder order = outboundOrderRepository.findByIdWithItems(id)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        // 只有已分配库存状态的出库单才能生成拣货任务
        if (order.getStatus() != 2) {
            throw new RuntimeException("只有已分配库存状态的出库单才能生成拣货任务");
        }

        // 生成拣货任务
        List<PickingTask> pickingTasks = new ArrayList<>();
        for (OutboundOrderItem item : order.getItems()) {
            // 查询已分配库存的库位
            List<Inventory> inventories = inventoryRepository.findByWarehouseIdAndProductSkuIdAndLockedQuantityGreaterThan(
                order.getWarehouseId(), item.getProductSkuId(), 0);

            for (Inventory inventory : inventories) {
                if (inventory.getLockedQuantity() <= 0) continue;

                PickingTask task = new PickingTask();
                task.setTaskNo(generateTaskNo());
                task.setOutboundOrderId(order.getId());
                task.setProductSkuId(item.getProductSkuId());
                task.setFromLocationId(inventory.getLocationId());
                task.setQuantity(inventory.getLockedQuantity());
                task.setStatus(1); // 待拣选
                task.setPickedQuantity(0);
                task.setCreatedTime(LocalDateTime.now());

                pickingTasks.add(task);
            }
        }

        pickingTaskRepository.saveAll(pickingTasks);

        // 更新出库单状态
        order.setStatus(3); // 拣货中
        order.setUpdatedTime(LocalDateTime.now());
        outboundOrderRepository.save(order);
    }

    /**
     * 获取拣货任务列表
     */
    public List<PickingTaskDTO> getPickingTasks(Long outboundOrderId) {
        List<PickingTask> tasks = pickingTaskRepository.findByOutboundOrderId(outboundOrderId);
        return tasks.stream()
            .map(PickingTaskMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * 完成拣货任务
     */
    @Transactional
    public void completePickingTask(Long taskId, Integer pickedQuantity) {
        PickingTask task = pickingTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("拣货任务不存在"));

        if (pickedQuantity > task.getQuantity()) {
            throw new RuntimeException("拣货数量不能超过任务数量");
        }

        task.setPickedQuantity(pickedQuantity);
        task.setStatus(pickedQuantity.equals(task.getQuantity()) ? 3 : 2); // 3:已完成, 2:部分完成
        task.setUpdatedTime(LocalDateTime.now());

        pickingTaskRepository.save(task);

        // 更新出库单明细的已拣选数量
        OutboundOrderItem item = outboundOrderItemRepository.findByOutboundOrderIdAndProductSkuId(
            task.getOutboundOrderId(), task.getProductSkuId())
            .orElseThrow(() -> new RuntimeException("出库单明细不存在"));

        // 计算该商品的总已拣选数量
        List<PickingTask> allTasks = pickingTaskRepository.findByOutboundOrderIdAndProductSkuId(
            task.getOutboundOrderId(), task.getProductSkuId());
        int totalPicked = allTasks.stream()
            .mapToInt(PickingTask::getPickedQuantity)
            .sum();

        item.setPickedQuantity(totalPicked);
        item.setUpdatedTime(LocalDateTime.now());
        outboundOrderItemRepository.save(item);

        // 检查是否所有任务都完成
        OutboundOrder order = outboundOrderRepository.findById(task.getOutboundOrderId())
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        List<PickingTask> allOrderTasks = pickingTaskRepository.findByOutboundOrderId(order.getId());
        boolean allCompleted = allOrderTasks.stream()
            .allMatch(t -> t.getStatus() == 3);

        if (allCompleted) {
            order.setStatus(4); // 已发货
            order.setUpdatedTime(LocalDateTime.now());
            outboundOrderRepository.save(order);
        }
    }

    /**
     * 确认发货
     */
    @Transactional
    public void confirmShipment(Long id, String trackingNumber) {
        OutboundOrder order = outboundOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));

        // 只有拣货中状态的出库单才能确认发货
        if (order.getStatus() != 3) {
            throw new RuntimeException("只有拣货中状态的出库单才能确认发货");
        }

        // 更新状态
        order.setStatus(4); // 已发货
        order.setUpdatedTime(LocalDateTime.now());
        outboundOrderRepository.save(order);

        // 扣减库存
        List<OutboundOrderItem> items = outboundOrderItemRepository.findByOutboundOrderId(id);
        for (OutboundOrderItem item : items) {
            List<Inventory> inventories = inventoryRepository.findByWarehouseIdAndProductSkuIdAndLockedQuantityGreaterThan(
                order.getWarehouseId(), item.getProductSkuId(), 0);

            int remainingQuantity = item.getPickedQuantity();
            for (Inventory inventory : inventories) {
                if (remainingQuantity <= 0) break;

                int deductQuantity = Math.min(remainingQuantity, inventory.getLockedQuantity());
                inventory.setQuantity(inventory.getQuantity() - deductQuantity);
                inventory.setLockedQuantity(inventory.getLockedQuantity() - deductQuantity);
                inventory.setUpdatedTime(LocalDateTime.now());
                inventoryRepository.save(inventory);

                remainingQuantity -= deductQuantity;
            }
        }
    }

    /**
     * 生成出库单号
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "OUT" + dateStr + uuid;
    }

    /**
     * 生成任务号
     */
    private String generateTaskNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TASK" + dateStr + uuid;
    }
}
