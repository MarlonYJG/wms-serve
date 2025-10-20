package com.bj.wms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建出库单请求
 */
@Data
public class OutboundOrderCreateRequest {
    
    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;
    
    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    
    private String customerInfo;
    
    @NotEmpty(message = "商品明细不能为空")
    @Valid
    private List<OutboundOrderItemCreateRequest> items;
    
    @Data
    public static class OutboundOrderItemCreateRequest {
        @NotNull(message = "商品SKU ID不能为空")
        private Long productSkuId;
        
        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}
