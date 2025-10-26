package com.bj.wms.entity;

import com.bj.wms.entity.converter.InboundStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@Table(name = "inbound_order")
@EqualsAndHashCode(callSuper = true)
public class InboundOrder extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @NotNull
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Convert(converter = InboundStatusConverter.class)
    @Column(name = "status", nullable = false)
    private InboundStatus status = InboundStatus.PENDING;

    @Column(name = "total_expected_quantity")
    private Integer totalExpectedQuantity;

    @Column(name = "total_received_quantity")
    private Integer totalReceivedQuantity = 0;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", insertable = false, updatable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "inboundOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InboundOrderItem> orderItems;
}


