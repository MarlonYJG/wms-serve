package com.bj.wms.dto;

import lombok.Data;

/**
 * 复核任务查询请求
 */
@Data
public class ReviewTaskQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String taskNo;
    private String outboundOrderNo;
    private Long outboundOrderId;
    private Long productSkuId;
    private Integer status;
    private String startTime;
    private String endTime;
}
