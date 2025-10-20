package com.bj.wms.dto;

import lombok.Data;

@Data
public class InboundOrderQueryRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String orderNo;
    private Long warehouseId;
    private Long supplierId;
    private Integer status;
    private String startTime;
    private String endTime;
}
