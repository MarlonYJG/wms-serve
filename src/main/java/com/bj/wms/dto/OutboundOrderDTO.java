package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单DTO
 */
@Data
public class OutboundOrderDTO {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private String statusText;
    private String statusName;
    private BigDecimal amountTotal;
    private BigDecimal chargeAmount;
    private BigDecimal totalAmount;
    private String customerInfo;
    private String remark;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
    
    /**
     * 出库单明细
     */
    private List<OutboundOrderItemDTO> items;
}