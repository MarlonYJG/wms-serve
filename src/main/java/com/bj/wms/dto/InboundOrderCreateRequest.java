package com.bj.wms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class InboundOrderCreateRequest {
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;
    
    @NotNull(message = "供应商ID不能为空")
    private Long supplierId;
    
    @Valid
    @NotNull(message = "明细不能为空")
    private List<InboundOrderItemCreateRequest> items;
}
