package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 入库费用DTO
 */
@Data
public class InboundChargeDTO {

    private Long id;

    /**
     * 入库单ID
     */
    private Long inboundOrderId;

    /**
     * 入库单号
     */
    private String inboundOrderNo;

    /**
     * 费用类型ID
     */
    private Long chargeType;

    /**
     * 费用类型名称
     */
    private String chargeTypeName;

    /**
     * 费用金额
     */
    private BigDecimal amount;

    /**
     * 税率（百分比）
     */
    private BigDecimal taxRate;

    /**
     * 币种
     */
    private String currency;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
