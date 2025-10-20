package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "inventory")
@EqualsAndHashCode(callSuper = true)
public class Inventory extends BaseEntity {

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @NotNull
    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "production_date")
    private java.time.LocalDate productionDate;

    @Column(name = "expiry_date")
    private java.time.LocalDate expiryDate;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "locked_quantity")
    private Integer lockedQuantity = 0;
}


