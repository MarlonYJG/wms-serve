package com.bj.wms.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单DTO
 */
@Data
public class OutboundOrderDTO {
    private Long id;
    private String orderNo;
    private Long warehouseId;
    private String warehouseName;
    private Long customerId;
    private String customerName;
    private Integer status;
    private String statusName;
    private String customerInfo;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private List<OutboundOrderItemDTO> items;
}
