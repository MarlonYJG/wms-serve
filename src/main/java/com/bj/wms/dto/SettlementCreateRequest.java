package com.bj.wms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建结算单请求
 */
@Data
public class SettlementCreateRequest {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String currency = "CNY";
    private String remark;

    /**
     * 出库单ID列表
     */
    private List<Long> outboundOrderIds;
}
