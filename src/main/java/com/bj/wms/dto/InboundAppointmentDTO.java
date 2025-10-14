package com.bj.wms.dto;

import com.bj.wms.entity.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InboundAppointmentDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String appointmentNo;

    @NotNull
    private Long warehouseId;

    @NotNull
    private Long supplierId;

    private LocalDateTime expectedArrivalTime;

    private AppointmentStatus status;

    private String remark;

    private Long createdTime;

    private Long updatedTime;
}


