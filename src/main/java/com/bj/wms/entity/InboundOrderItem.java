package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "inbound_order_item")
@EqualsAndHashCode(callSuper = true)
public class InboundOrderItem extends BaseEntity {

    @Column(name = "inbound_order_id", nullable = false, insertable = false, updatable = false)
    private Long inboundOrderId;

    @NotNull
    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @NotNull
    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "received_quantity")
    private Integer receivedQuantity = 0;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_order_id")
    private InboundOrder inboundOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", insertable = false, updatable = false)
    private ProductSku productSku;
}


