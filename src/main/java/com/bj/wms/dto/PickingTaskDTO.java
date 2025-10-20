package com.bj.wms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拣货任务DTO
 */
@Data
public class PickingTaskDTO {
    private Long id;
    private String taskNo;
    private String waveNo;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private Long fromLocationId;
    private String fromLocationCode;
    private Integer quantity;
    private Integer status;
    private String statusName;
    private Integer pickedQuantity;
    private LocalDateTime createdTime;
}
