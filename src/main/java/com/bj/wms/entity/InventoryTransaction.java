package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_transaction")
@EqualsAndHashCode(callSuper = true)
public class InventoryTransaction extends BaseEntity {

    @NotNull
    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @Column(name = "batch_no")
    private String batchNo;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @NotNull
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    /** 1入库 2出库 3移库 4盘点调整 */
    @NotNull
    @Column(name = "transaction_type", nullable = false, columnDefinition = "TINYINT")
    private Integer transactionType;

    @Column(name = "related_order_no")
    private String relatedOrderNo;

    @NotNull
    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @NotNull
    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "operator")
    private Integer operator;
}


