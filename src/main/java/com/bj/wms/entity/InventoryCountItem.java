package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "inventory_count_item")
@EqualsAndHashCode(callSuper = true)
public class InventoryCountItem extends BaseEntity {

    @NotNull
    @Column(name = "count_id", nullable = false)
    private Long countId;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @NotNull
    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "system_qty")
    private Integer systemQty;

    @Column(name = "counted_qty")
    private Integer countedQty;

    @Column(name = "difference_qty")
    private Integer differenceQty;
}


