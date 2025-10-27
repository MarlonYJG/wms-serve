package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 利润计算DTO
 */
@Data
public class ProfitCalculationDTO {
    
    private Long settlementId;
    private Long outboundOrderId;
    private Long customerId;
    private String customerName;
    
    /**
     * 收入部分
     */
    private RevenueDTO revenue;
    
    /**
     * 成本部分
     */
    private CostsDTO costs;
    
    /**
     * 利润计算
     */
    private ProfitDTO profit;
    
    @Data
    public static class RevenueDTO {
        /**
         * 商品收入
         */
        private BigDecimal goodsAmount = BigDecimal.ZERO;
        
        /**
         * 服务收入
         */
        private BigDecimal serviceAmount = BigDecimal.ZERO;
        
        /**
         * 总收入
         */
        private BigDecimal totalRevenue = BigDecimal.ZERO;
    }
    
    @Data
    public static class CostsDTO {
        /**
         * 商品成本
         */
        private BigDecimal goodsCost = BigDecimal.ZERO;
        
        /**
         * 仓储成本
         */
        private BigDecimal storageCost = BigDecimal.ZERO;
        
        /**
         * 操作成本
         */
        private BigDecimal handlingCost = BigDecimal.ZERO;
        
        /**
         * 运输成本
         */
        private BigDecimal shippingCost = BigDecimal.ZERO;
        
        /**
         * 其他成本
         */
        private BigDecimal otherCosts = BigDecimal.ZERO;
        
        /**
         * 总成本
         */
        private BigDecimal totalCosts = BigDecimal.ZERO;
    }
    
    @Data
    public static class ProfitDTO {
        /**
         * 毛利润 = 总收入 - 总成本
         */
        private BigDecimal grossProfit = BigDecimal.ZERO;
        
        /**
         * 利润率 = 毛利润 / 总收入 * 100%
         */
        private BigDecimal profitMargin = BigDecimal.ZERO;
        
        /**
         * 净利润（扣除税费等）
         */
        private BigDecimal netProfit = BigDecimal.ZERO;
    }
}
