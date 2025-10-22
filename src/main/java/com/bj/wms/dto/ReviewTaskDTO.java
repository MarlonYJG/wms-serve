package com.bj.wms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 复核任务DTO
 */
@Data
public class ReviewTaskDTO {
    private Long id;
    private String taskNo;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private Integer expectedQuantity;
    private Integer actualQuantity;
    private Integer status;
    private String statusName;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime reviewTime;
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
