package com.bj.wms.dto;

import lombok.Data;

/**
 * 打包任务查询请求
 */
@Data
public class PackingTaskQueryRequest {
    private String taskNo;
    private Long outboundOrderId;
    private String outboundOrderNo;
    private Long packingMaterialId;
    private Integer status;
    private Long packerId;
    private String packerName;
    private Integer page = 1;
    private Integer size = 10;
}
