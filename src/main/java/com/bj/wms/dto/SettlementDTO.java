package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 结算单DTO
 */
@Data
public class SettlementDTO {

    private Long id;
    private String settlementNo;
    private Long customerId;
    private String customerName;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Integer status;
    private String statusText;
    private String currency;
    private BigDecimal amountGoods;
    private BigDecimal amountCharges;
    private BigDecimal amountTotal;
    private String remark;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
    
    // 结算明细列表
    private List<SettlementItemDTO> items;
}
