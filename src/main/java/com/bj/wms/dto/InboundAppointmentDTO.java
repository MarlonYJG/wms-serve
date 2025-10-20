package com.bj.wms.dto;

import com.bj.wms.entity.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class InboundAppointmentDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String appointmentNo;

    @NotNull
    private Long warehouseId;
    
    private String warehouseName;

    @NotNull
    private Long supplierId;
    
    private String supplierName;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTimeStart;

    @NotNull
    private LocalTime appointmentTimeEnd;

    private AppointmentStatus status;
    
    private String statusName;

    private Integer totalExpectedQuantity;

    @Size(max = 500)
    private String specialRequirements;

    private Long approvedBy;
    
    private String approvedByName;

    private LocalDateTime approvedTime;

    @Size(max = 255)
    private String remark;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
    
    private List<InboundAppointmentItemDTO> appointmentItems;
}


