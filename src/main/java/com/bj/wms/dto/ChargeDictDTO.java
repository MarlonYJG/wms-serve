package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 费用字典DTO
 */
@Data
public class ChargeDictDTO {

    private Long id;
    private String chargeCode;
    private String chargeName;
    private BigDecimal defaultTaxRate;
    private Boolean isEnabled;
    private String remark;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
