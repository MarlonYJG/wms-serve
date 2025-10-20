package com.bj.wms.entity;

import com.bj.wms.entity.converter.AppointmentStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @NotNull
    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @NotNull
    @Column(name = "appointment_time_start", nullable = false)
    private LocalTime appointmentTimeStart;

    @NotNull
    @Column(name = "appointment_time_end", nullable = false)
    private LocalTime appointmentTimeEnd;

    @Convert(converter = AppointmentStatusConverter.class)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(name = "total_expected_quantity")
    private Integer totalExpectedQuantity;

    @Size(max = 500)
    @Column(name = "special_requirements", length = 500)
    private String specialRequirements;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_time")
    private LocalDateTime approvedTime;

    @Size(max = 255)
    @Column(name = "remark", length = 255)
    private String remark;

    // 关联关系
    @OneToMany(mappedBy = "inboundAppointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InboundAppointmentItem> appointmentItems;
}


