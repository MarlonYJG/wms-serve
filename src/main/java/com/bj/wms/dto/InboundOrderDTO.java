package com.bj.wms.dto;

import com.bj.wms.entity.InboundStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InboundOrderDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String orderNo;

    @NotNull
    private Long warehouseId;

    @NotNull
    private Long supplierId;

    private InboundStatus status;

    private Integer totalExpectedQuantity;

    private Integer totalReceivedQuantity;

    private Long createdTime;

    private Long updatedTime;
}


