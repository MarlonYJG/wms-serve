package com.bj.wms.dto;

import com.bj.wms.entity.QcStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InboundQcDTO {
    private Long id;

    @NotNull
    private Long inboundOrderItemId;

    private QcStatus status;

    private Integer qualifiedQuantity;

    private Integer unqualifiedQuantity;

    private String remark;

    private Long createdTime;

    private Long updatedTime;
}


