package com.bj.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PutawayTaskDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String taskNo;

    @NotNull
    private Long inboundOrderItemId;

    private Long fromLocationId;

    @NotNull
    private Long toLocationId;

    @NotNull
    private Integer quantity;

    private Integer status;

    private Integer operator;

    private Long createdTime;

    private Long updatedTime;
}


