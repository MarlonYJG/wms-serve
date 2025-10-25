package com.bj.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建费用字典请求
 */
@Data
public class ChargeDictCreateRequest {

    @NotBlank(message = "费用编码不能为空")
    private String chargeCode;

    @NotBlank(message = "费用名称不能为空")
    private String chargeName;

    private BigDecimal defaultTaxRate;
    private Boolean isEnabled = true;
    private String remark;
}
