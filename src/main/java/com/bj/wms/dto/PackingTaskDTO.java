package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打包任务DTO
 */
@Data
public class PackingTaskDTO {
    private Long id;
    private String taskNo;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private Long packingMaterialId;
    private String packingMaterialName;
    private String packingMaterialCode;
    private BigDecimal weight;
    private BigDecimal volume;
    private String dimensions;
    private Integer status;
    private String statusName;
    private Long packerId;
    private String packerName;
    private LocalDateTime packedTime;
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
