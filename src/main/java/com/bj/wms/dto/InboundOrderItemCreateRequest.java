package com.bj.wms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InboundOrderItemCreateRequest {
    @NotNull(message = "商品SKU ID不能为空")
    private Long productSkuId;
    
    @NotNull(message = "预期数量不能为空")
    @Positive(message = "预期数量必须大于0")
    private Integer expectedQuantity;
    
    private BigDecimal unitPrice;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expiryDate;
}
