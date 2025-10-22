package com.bj.wms.dto;

import lombok.Data;

/**
 * 拣货任务查询请求
 */
@Data
public class PickingTaskQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String taskNo;
    private String waveNo;
    private Long outboundOrderId;
    private Long productSkuId;
    private Long fromLocationId;
    private Integer status;
    private String startTime;
    private String endTime;
}
