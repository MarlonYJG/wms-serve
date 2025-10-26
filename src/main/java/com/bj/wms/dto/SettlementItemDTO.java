package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 结算明细DTO
 */
@Data
public class SettlementItemDTO {
    private Long id;
    private Long settlementId;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private BigDecimal amountGoods;
    private BigDecimal amountCharges;
    private BigDecimal amountTotal;
    private String remark;
}