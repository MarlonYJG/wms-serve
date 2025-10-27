package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 结算单利润汇总DTO
 */
@Data
public class SettlementProfitSummaryDTO {
    
    private Long settlementId;
    
    /**
     * 总收入
     */
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    
    /**
     * 总成本
     */
    private BigDecimal totalCosts = BigDecimal.ZERO;
    
    /**
     * 总利润
     */
    private BigDecimal totalProfit = BigDecimal.ZERO;
    
    /**
     * 平均利润率
     */
    private BigDecimal averageProfitMargin = BigDecimal.ZERO;
    
    /**
     * 项目数量
     */
    private Integer itemCount = 0;
    
    /**
     * 利润计算明细列表
     */
    private List<ProfitCalculationDTO> items;
}
