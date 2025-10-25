package com.bj.wms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 结算单查询请求
 */
@Data
public class SettlementQueryRequest {

    private Integer page = 1;
    private Integer size = 10;
    private String settlementNo;
    private Long customerId;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
