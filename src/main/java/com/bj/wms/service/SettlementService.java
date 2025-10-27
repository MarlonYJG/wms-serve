package com.bj.wms.service;

import com.bj.wms.dto.*;
import com.bj.wms.entity.*;
import com.bj.wms.mapper.SettlementMapper;
import com.bj.wms.repository.*;
import com.bj.wms.util.PageResult;
import com.bj.wms.util.EncodingTestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 结算单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementItemRepository settlementItemRepository;
    private final OutboundOrderRepository outboundOrderRepository;
    private final OutboundOrderItemRepository outboundOrderItemRepository;
    private final OutboundChargeRepository outboundChargeRepository;
    private final CustomerRepository customerRepository;

    /**
     * 分页查询结算单
     */
    public PageResult<SettlementDTO> getSettlementList(SettlementQueryRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<Settlement> page = settlementRepository.findSettlements(
            request.getSettlementNo(),
            request.getCustomerId(),
            request.getStatus(),
            request.getStartTime(),
            request.getEndTime(),
            pageable
        );

        List<SettlementDTO> dtoList = SettlementMapper.INSTANCE.toDTOList(page.getContent());
        return new PageResult<>(dtoList, request.getPage(), request.getSize(), page.getTotalElements());
    }

    /**
     * 获取结算单详情
     */
    public SettlementDTO getSettlementById(Long id) {
        Settlement settlement = settlementRepository.findByIdWithCustomer(id)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        SettlementDTO dto = SettlementMapper.INSTANCE.toDTO(settlement);
        
        // 设置客户名称
        if (settlement.getCustomer() != null) {
            dto.setCustomerName(settlement.getCustomer().getCustomerName());
        }
        
        // 获取结算明细
        List<SettlementItem> items = settlementItemRepository.findBySettlementIdAndDeletedFalse(id);
        dto.setItems(SettlementMapper.INSTANCE.toItemDTOList(items));

        return dto;
    }

    /**
     * 创建结算单
     */
    @Transactional
    public SettlementDTO createSettlement(SettlementCreateRequest request) {
        // 验证客户是否存在
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new RuntimeException("客户不存在"));

        // 生成结算单号
        String settlementNo = generateSettlementNo();

        // 创建结算单
        Settlement settlement = new Settlement();
        settlement.setSettlementNo(settlementNo);
        settlement.setCustomerId(request.getCustomerId());
        settlement.setPeriodStart(request.getPeriodStart());
        settlement.setPeriodEnd(request.getPeriodEnd());
        settlement.setStatus(1); // 草稿
        settlement.setCurrency(request.getCurrency());
        settlement.setRemark(request.getRemark());
        settlement.setCreatedTime(LocalDateTime.now());
        settlement.setUpdatedTime(LocalDateTime.now());

        settlement = settlementRepository.save(settlement);

        // 添加出库单明细
        if (request.getOutboundOrderIds() != null && !request.getOutboundOrderIds().isEmpty()) {
            addOutboundOrdersToSettlement(settlement.getId(), request.getOutboundOrderIds());
        }

        return getSettlementById(settlement.getId());
    }

    /**
     * 添加出库单到结算单
     */
    @Transactional
    public void addOutboundOrdersToSettlement(Long settlementId, List<Long> outboundOrderIds) {
        Settlement settlement = settlementRepository.findById(settlementId)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        if (settlement.getStatus() != 1) {
            throw new RuntimeException("只有草稿状态的结算单才能添加出库单");
        }

        for (Long outboundOrderId : outboundOrderIds) {
            // 检查出库单是否已存在
            if (settlementItemRepository.existsByOutboundOrderIdAndSettlementId(outboundOrderId, settlementId)) {
                continue;
            }

            // 验证出库单是否存在
            OutboundOrder outboundOrder = outboundOrderRepository.findById(outboundOrderId)
                .orElseThrow(() -> new RuntimeException("出库单不存在: " + outboundOrderId));

            // 计算出库单金额
            BigDecimal amountGoods = calculateOutboundOrderGoodsAmount(outboundOrderId);
            BigDecimal amountCharges = BigDecimal.valueOf(outboundChargeRepository.sumAmountByOutboundOrderId(outboundOrderId));
            BigDecimal amountTotal = amountGoods.add(amountCharges);

            // 创建结算明细
            SettlementItem item = new SettlementItem();
            item.setSettlementId(settlementId);
            item.setOutboundOrderId(outboundOrderId);
            item.setAmountGoods(amountGoods);
            item.setAmountCharges(amountCharges);
            item.setAmountTotal(amountTotal);
            item.setCreatedTime(LocalDateTime.now());
            item.setUpdatedTime(LocalDateTime.now());

            settlementItemRepository.save(item);
        }

        // 重新计算结算单总金额
        recalculateSettlementAmount(settlementId);
    }

    /**
     * 从结算单移除出库单
     */
    @Transactional
    public void removeOutboundOrderFromSettlement(Long settlementId, Long outboundOrderId) {
        Settlement settlement = settlementRepository.findById(settlementId)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        if (settlement.getStatus() != 1) {
            throw new RuntimeException("只有草稿状态的结算单才能移除出库单");
        }

        settlementItemRepository.deleteBySettlementIdAndOutboundOrderId(settlementId, outboundOrderId);
        
        // 重新计算结算单总金额
        recalculateSettlementAmount(settlementId);
    }

    /**
     * 提交结算单
     */
    @Transactional
    public void submitSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        if (settlement.getStatus() != 1) {
            throw new RuntimeException("只有草稿状态的结算单才能提交");
        }

        // 检查是否有明细
        List<SettlementItem> items = settlementItemRepository.findBySettlementIdAndDeletedFalse(id);
        if (items.isEmpty()) {
            throw new RuntimeException("结算单必须包含至少一条明细才能提交");
        }

        settlement.setStatus(2); // 待审核
        settlement.setUpdatedTime(LocalDateTime.now());
        settlementRepository.save(settlement);

        log.info("结算单 {} 已提交审核", settlement.getSettlementNo());
    }

    /**
     * 审核结算单
     */
    @Transactional
    public void approveSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        if (settlement.getStatus() != 2) {
            throw new RuntimeException("只有待审核状态的结算单才能审核");
        }

        settlement.setStatus(3); // 已审核
        settlement.setUpdatedTime(LocalDateTime.now());
        settlementRepository.save(settlement);

        log.info("结算单 {} 已审核通过", settlement.getSettlementNo());
    }

    /**
     * 结清结算单
     */
    @Transactional
    public void closeSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        if (settlement.getStatus() != 3) {
            throw new RuntimeException("只有已审核状态的结算单才能结清");
        }

        settlement.setStatus(4); // 已结清
        settlement.setUpdatedTime(LocalDateTime.now());
        settlementRepository.save(settlement);

        log.info("结算单 {} 已结清", settlement.getSettlementNo());
    }

    /**
     * 作废结算单
     */
    @Transactional
    public void voidSettlement(Long id) {
        Settlement settlement = settlementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));

        if (settlement.getStatus() == 4) {
            throw new RuntimeException("已结清的结算单不能作废");
        }

        settlement.setStatus(5); // 已作废
        settlement.setUpdatedTime(LocalDateTime.now());
        settlementRepository.save(settlement);

        log.info("结算单 {} 已作废", settlement.getSettlementNo());
    }

    /**
     * 重新计算结算单总金额
     */
    private void recalculateSettlementAmount(Long settlementId) {
        List<SettlementItem> items = settlementItemRepository.findBySettlementIdAndDeletedFalse(settlementId);
        
        BigDecimal totalGoods = items.stream()
            .map(SettlementItem::getAmountGoods)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCharges = items.stream()
            .map(SettlementItem::getAmountCharges)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalAmount = totalGoods.add(totalCharges);

        Settlement settlement = settlementRepository.findById(settlementId).orElse(null);
        if (settlement != null) {
            settlement.setAmountGoods(totalGoods);
            settlement.setAmountCharges(totalCharges);
            settlement.setAmountTotal(totalAmount);
            settlement.setUpdatedTime(LocalDateTime.now());
            settlementRepository.save(settlement);
        }
    }

    /**
     * 获取可结算的出库单列表
     */
    public List<OutboundOrderDTO> getSettlableOutboundOrders(Long customerId) {
        // 查询已发货但未结算的出库单
        List<OutboundOrder> orders = outboundOrderRepository.findByCustomerIdAndStatusAndSettledFalse(customerId, 4);
        return orders.stream()
            .map(order -> {
                OutboundOrderDTO dto = new OutboundOrderDTO();
                dto.setId(order.getId());
                dto.setOrderNo(order.getOrderNo());
                dto.setCustomerId(order.getCustomerId());
                dto.setStatus(order.getStatus());
                dto.setCreatedTime(order.getCreatedTime());
                
                // 计算商品金额（通过出库单明细计算）
                BigDecimal goodsAmount = calculateOutboundOrderGoodsAmount(order.getId());
                dto.setAmountTotal(goodsAmount);
                
                // 计算费用金额
                BigDecimal chargeAmount = BigDecimal.valueOf(outboundChargeRepository.sumAmountByOutboundOrderId(order.getId()));
                dto.setChargeAmount(chargeAmount);
                
                // 计算总金额（商品金额 + 费用金额）
                BigDecimal totalAmount = goodsAmount.add(chargeAmount);
                dto.setTotalAmount(totalAmount);
                
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取所有可结算的出库单列表（支持分页和搜索）
     */
    public PageResult<OutboundOrderDTO> getAllSettlableOutboundOrders(OutboundOrderQueryRequest request) {
        // 构建查询条件
        Specification<OutboundOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 只查询已发货的出库单
            predicates.add(cb.equal(root.get("status"), 4));
            
            // 客户筛选
            if (request.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customerId"), request.getCustomerId()));
            }
            
            // 出库单号筛选
            if (request.getOrderNo() != null && !request.getOrderNo().trim().isEmpty()) {
                predicates.add(cb.like(root.get("orderNo"), "%" + request.getOrderNo() + "%"));
            }
            
            // 时间范围筛选
            if (request.getStartTime() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdTime"), request.getStartTime()));
            }
            if (request.getEndTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdTime"), request.getEndTime()));
            }
            
            // 排除已结算的出库单
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<SettlementItem> subRoot = subquery.from(SettlementItem.class);
            subquery.select(subRoot.get("outboundOrderId"))
                   .where(cb.equal(subRoot.get("deleted"), 0));
            predicates.add(cb.not(root.get("id").in(subquery)));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<OutboundOrder> page = outboundOrderRepository.findAll(spec, pageable);
        
        List<OutboundOrderDTO> dtoList = page.getContent().stream()
            .map(order -> {
                OutboundOrderDTO dto = new OutboundOrderDTO();
                dto.setId(order.getId());
                dto.setOrderNo(order.getOrderNo());
                dto.setCustomerId(order.getCustomerId());
                dto.setStatus(order.getStatus());
                dto.setCreatedTime(order.getCreatedTime());
                
                // 计算商品金额（通过出库单明细计算）
                BigDecimal goodsAmount = calculateOutboundOrderGoodsAmount(order.getId());
                dto.setAmountTotal(goodsAmount);
                
                // 计算费用金额
                BigDecimal chargeAmount = BigDecimal.valueOf(outboundChargeRepository.sumAmountByOutboundOrderId(order.getId()));
                dto.setChargeAmount(chargeAmount);
                
                // 计算总金额（商品金额 + 费用金额）
                BigDecimal totalAmount = goodsAmount.add(chargeAmount);
                dto.setTotalAmount(totalAmount);
                
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
        
        return new PageResult<>(dtoList, request.getPage(), request.getSize(), page.getTotalElements());
    }

    /**
     * 计算出库单商品金额
     */
    private BigDecimal calculateOutboundOrderGoodsAmount(Long outboundOrderId) {
        // 查询出库单明细
        List<OutboundOrderItem> items = outboundOrderItemRepository.findByOutboundOrderIdAndDeletedFalse(outboundOrderId);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OutboundOrderItem item : items) {
            if (item.getProductSku() != null && item.getQuantity() != null) {
                // 商品金额 = 数量 × 销售价格
                BigDecimal itemAmount = item.getProductSku().getSalePrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemAmount);
            }
        }
        
        return totalAmount;
    }

    /**
     * 计算结算单利润
     */
    public SettlementProfitSummaryDTO calculateSettlementProfit(Long settlementId) {
        // 测试编码（仅在开发环境）
        if (log.isDebugEnabled()) {
            EncodingTestUtil.testChineseEncoding();
        }
        
        log.info("开始计算结算单利润，结算单ID: {}", settlementId);
        
        // 使用能够加载客户信息的查询
        Settlement settlement = settlementRepository.findByIdWithCustomer(settlementId)
            .orElseThrow(() -> new RuntimeException("结算单不存在"));
        
        SettlementProfitSummaryDTO summary = new SettlementProfitSummaryDTO();
        summary.setSettlementId(settlementId);
        
        // 获取结算单明细
        List<SettlementItem> items = settlementItemRepository.findBySettlementIdAndDeletedFalse(settlementId);
        
        List<ProfitCalculationDTO> profitItems = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCosts = BigDecimal.ZERO;
        
        for (SettlementItem item : items) {
            ProfitCalculationDTO profitItem = calculateOrderProfit(item.getOutboundOrderId());
            profitItem.setSettlementId(settlementId);
            profitItem.setCustomerId(settlement.getCustomerId());
            
            // 设置客户名称
            if (settlement.getCustomer() != null) {
                profitItem.setCustomerName(settlement.getCustomer().getCustomerName());
            }
            
            profitItems.add(profitItem);
            
            totalRevenue = totalRevenue.add(profitItem.getRevenue().getTotalRevenue());
            totalCosts = totalCosts.add(profitItem.getCosts().getTotalCosts());
        }
        
        summary.setItems(profitItems);
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalCosts(totalCosts);
        summary.setTotalProfit(totalRevenue.subtract(totalCosts));
        summary.setItemCount(items.size());
        
        // 计算平均利润率
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal averageMargin = summary.getTotalProfit()
                .divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            summary.setAverageProfitMargin(averageMargin);
        } else {
            summary.setAverageProfitMargin(BigDecimal.ZERO);
        }
        
        log.info("结算单利润计算完成，总收入: {}, 总成本: {}, 总利润: {}", 
                totalRevenue, totalCosts, summary.getTotalProfit());
        
        return summary;
    }
    
    /**
     * 计算单个出库单利润
     */
    public ProfitCalculationDTO calculateOrderProfit(Long outboundOrderId) {
        log.info("开始计算出库单利润，出库单ID: {}", outboundOrderId);
        
        OutboundOrder order = outboundOrderRepository.findByIdWithItems(outboundOrderId)
            .orElseThrow(() -> new RuntimeException("出库单不存在"));
        
        ProfitCalculationDTO profit = new ProfitCalculationDTO();
        profit.setOutboundOrderId(outboundOrderId);
        profit.setCustomerId(order.getCustomerId());
        
        // 设置客户名称
        if (order.getCustomer() != null) {
            profit.setCustomerName(order.getCustomer().getCustomerName());
        } else {
            // 如果出库单没有关联客户信息，通过客户ID查询
            Customer customer = customerRepository.findById(order.getCustomerId()).orElse(null);
            if (customer != null) {
                profit.setCustomerName(customer.getCustomerName());
            }
        }
        
        // 初始化收入和成本对象
        ProfitCalculationDTO.RevenueDTO revenue = new ProfitCalculationDTO.RevenueDTO();
        ProfitCalculationDTO.CostsDTO costs = new ProfitCalculationDTO.CostsDTO();
        ProfitCalculationDTO.ProfitDTO profitCalc = new ProfitCalculationDTO.ProfitDTO();
        
        // 计算收入
        calculateRevenue(outboundOrderId, revenue);
        
        // 计算成本
        calculateCosts(outboundOrderId, costs);
        
        // 计算利润
        profitCalc.setGrossProfit(revenue.getTotalRevenue().subtract(costs.getTotalCosts()));
        
        // 计算利润率
        if (revenue.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margin = profitCalc.getGrossProfit()
                .divide(revenue.getTotalRevenue(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            profitCalc.setProfitMargin(margin);
        } else {
            profitCalc.setProfitMargin(BigDecimal.ZERO);
        }
        
        // 净利润暂时等于毛利润（后续可扣除税费等）
        profitCalc.setNetProfit(profitCalc.getGrossProfit());
        
        profit.setRevenue(revenue);
        profit.setCosts(costs);
        profit.setProfit(profitCalc);
        
        log.info("出库单利润计算完成，总收入: {}, 总成本: {}, 毛利润: {}, 利润率: {}%", 
                revenue.getTotalRevenue(), costs.getTotalCosts(), 
                profitCalc.getGrossProfit(), profitCalc.getProfitMargin());
        
        return profit;
    }
    
    /**
     * 批量计算利润
     */
    public List<SettlementProfitSummaryDTO> batchCalculateProfit(List<Long> settlementIds) {
        log.info("开始批量计算利润，结算单数量: {}", settlementIds.size());
        
        List<SettlementProfitSummaryDTO> results = new ArrayList<>();
        
        for (Long settlementId : settlementIds) {
            try {
                SettlementProfitSummaryDTO summary = calculateSettlementProfit(settlementId);
                results.add(summary);
            } catch (Exception e) {
                log.error("计算结算单利润失败，结算单ID: {}, 错误: {}", settlementId, e.getMessage());
                // 继续处理其他结算单
            }
        }
        
        log.info("批量计算利润完成，成功计算: {} 个结算单", results.size());
        return results;
    }
    
    /**
     * 计算收入
     */
    private void calculateRevenue(Long outboundOrderId, ProfitCalculationDTO.RevenueDTO revenue) {
        // 商品收入 = 出库单明细的商品金额
        BigDecimal goodsAmount = calculateOutboundOrderGoodsAmount(outboundOrderId);
        revenue.setGoodsAmount(goodsAmount);
        
        // 服务收入 = 出库单费用
        BigDecimal serviceAmount = BigDecimal.valueOf(
            outboundChargeRepository.sumAmountByOutboundOrderId(outboundOrderId));
        revenue.setServiceAmount(serviceAmount);
        
        // 总收入
        revenue.setTotalRevenue(goodsAmount.add(serviceAmount));
    }
    
    /**
     * 计算成本
     */
    private void calculateCosts(Long outboundOrderId, ProfitCalculationDTO.CostsDTO costs) {
        // 获取出库单明细
        List<OutboundOrderItem> items = outboundOrderItemRepository.findByOutboundOrderIdAndDeletedFalse(outboundOrderId);
        
        BigDecimal goodsCost = BigDecimal.ZERO;
        BigDecimal storageCost = BigDecimal.ZERO;
        BigDecimal handlingCost = BigDecimal.ZERO;
        BigDecimal shippingCost = BigDecimal.ZERO;
        BigDecimal otherCosts = BigDecimal.ZERO;
        
        for (OutboundOrderItem item : items) {
            if (item.getProductSku() != null && item.getQuantity() != null) {
                // 商品成本 = 数量 × 成本价格
                BigDecimal itemCost = item.getProductSku().getCostPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                goodsCost = goodsCost.add(itemCost);
                
                // TODO: 这里可以根据实际业务需求计算其他成本
                // 例如：仓储成本、操作成本、运输成本等
                // 目前暂时设为0，后续可以根据实际业务规则计算
            }
        }
        
        costs.setGoodsCost(goodsCost);
        costs.setStorageCost(storageCost);
        costs.setHandlingCost(handlingCost);
        costs.setShippingCost(shippingCost);
        costs.setOtherCosts(otherCosts);
        
        // 总成本
        costs.setTotalCosts(goodsCost.add(storageCost).add(handlingCost)
            .add(shippingCost).add(otherCosts));
    }
    
    /**
     * 生成结算单号
     */
    private String generateSettlementNo() {
        String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ST" + dateStr + uuid;
    }
}
