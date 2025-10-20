package com.bj.wms.dto;

import lombok.Data;

/**
 * 出库单查询请求
 */
@Data
public class OutboundOrderQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String orderNo;
    private Long warehouseId;
    private Long customerId;
    private Integer status;
    private String startTime;
    private String endTime;
}
