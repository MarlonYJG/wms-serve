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
    private String fromLocationCode;

    private Long toLocationId;
    private String toLocationCode;

    @NotNull
    private Integer quantity;

    private Integer status;
    private String statusName;

    private Integer operator;

    // 关联信息
    private Long productSkuId;
    private String skuCode;
    private String productName;

    private Long createdTime;
    private Long updatedTime;
}


