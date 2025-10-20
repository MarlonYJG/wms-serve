package com.bj.wms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 出库单明细DTO
 */
@Data
public class OutboundOrderItemDTO {
    private Long id;
    private Long outboundOrderId;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private Integer quantity;
    private Integer allocatedQuantity;
    private Integer pickedQuantity;
    private LocalDateTime createdTime;
}
