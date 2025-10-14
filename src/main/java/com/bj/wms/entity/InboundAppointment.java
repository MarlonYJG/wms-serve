package com.bj.wms.entity;

import com.bj.wms.entity.converter.AppointmentStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inbound_appointment")
@EqualsAndHashCode(callSuper = true)
public class InboundAppointment extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "appointment_no", nullable = false, unique = true, length = 50)
    private String appointmentNo;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @NotNull
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "expected_arrival_time")
    private LocalDateTime expectedArrivalTime;

    @Convert(converter = AppointmentStatusConverter.class)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Size(max = 255)
    @Column(name = "remark", length = 255)
    private String remark;
}


