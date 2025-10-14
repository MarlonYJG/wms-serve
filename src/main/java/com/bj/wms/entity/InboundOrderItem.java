package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "inbound_order_item")
@EqualsAndHashCode(callSuper = true)
public class InboundOrderItem extends BaseEntity {

    @NotNull
    @Column(name = "inbound_order_id", nullable = false)
    private Long inboundOrderId;

    @NotNull
    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @NotNull
    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "received_quantity")
    private Integer receivedQuantity = 0;
}


