package com.bj.wms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建入库费用请求
 */
@Data
public class InboundChargeCreateRequest {

    @NotNull(message = "入库单ID不能为空")
    private Long inboundOrderId;

    @NotNull(message = "费用类型不能为空")
    private Long chargeType;

    @NotNull(message = "费用金额不能为空")
    @Positive(message = "费用金额必须大于0")
    private BigDecimal amount;

    private BigDecimal taxRate;
    private String currency = "CNY";
    private String remark;
}
