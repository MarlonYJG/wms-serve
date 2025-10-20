package com.bj.wms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class InboundAppointmentCreateRequest {
    
    @NotNull
    private Long warehouseId;

    @NotNull
    private Long supplierId;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTimeStart;

    @NotNull
    private LocalTime appointmentTimeEnd;

    @Size(max = 500)
    private String specialRequirements;

    @Valid
    @NotNull
    @Size(min = 1, message = "预约商品明细不能为空")
    private List<InboundAppointmentItemCreateRequest> appointmentItems;
}
