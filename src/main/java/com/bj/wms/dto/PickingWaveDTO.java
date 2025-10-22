package com.bj.wms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 拣货波次DTO
 */
@Data
public class PickingWaveDTO {
    private Long id;
    private String waveNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private String statusName;
    private Integer orderCount;
    private Integer taskCount;
    private Integer completedTaskCount;
    private LocalDateTime startedTime;
    private LocalDateTime completedTime;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
