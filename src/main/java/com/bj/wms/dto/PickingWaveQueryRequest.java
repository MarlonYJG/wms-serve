package com.bj.wms.dto;

import lombok.Data;

/**
 * 拣货波次查询请求
 */
@Data
public class PickingWaveQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String waveNo;
    private Long warehouseId;
    private Integer status;
    private String startTime;
    private String endTime;
}
