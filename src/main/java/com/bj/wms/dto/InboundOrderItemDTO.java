package com.bj.wms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InboundOrderItemDTO {
    private Long id;

    @NotNull
    private Long inboundOrderId;

    @NotNull
    private Long productSkuId;

    @NotNull
    private Integer expectedQuantity;

    private Integer receivedQuantity;

    private Long createdTime;

    private Long updatedTime;
}


