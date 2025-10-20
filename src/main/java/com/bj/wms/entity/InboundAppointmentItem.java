package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "inbound_appointment_item")
@EqualsAndHashCode(callSuper = true)
public class InboundAppointmentItem extends BaseEntity {

    @NotNull
    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @NotNull
    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @NotNull
    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "batch_no", length = 100)
    private String batchNo;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", insertable = false, updatable = false)
    private InboundAppointment inboundAppointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", insertable = false, updatable = false)
    private ProductSku productSku;
}
