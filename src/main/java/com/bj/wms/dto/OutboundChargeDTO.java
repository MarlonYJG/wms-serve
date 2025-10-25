package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库费用DTO
 */
@Data
public class OutboundChargeDTO {

    private Long id;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private Long chargeType;
    private String chargeTypeName;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private String currency;
    private String remark;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
