package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建打包任务请求
 */
@Data
public class PackingTaskCreateRequest {
    private Long outboundOrderId;
    private Long packingMaterialId;
    private String remark;
}
