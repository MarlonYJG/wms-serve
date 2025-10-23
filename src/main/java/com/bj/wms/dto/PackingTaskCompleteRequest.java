package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 完成打包任务请求
 */
@Data
public class PackingTaskCompleteRequest {
    private BigDecimal weight;
    private BigDecimal volume;
    private String dimensions;
    private Long packerId;
    private String packerName;
    private String remark;
}
