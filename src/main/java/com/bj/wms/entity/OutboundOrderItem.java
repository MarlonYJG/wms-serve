package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出库单明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outbound_order_item")
public class OutboundOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outbound_order_id", nullable = false)
    private Long outboundOrderId;

    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 需求数量

    @Column(name = "allocated_quantity")
    private Integer allocatedQuantity = 0; // 已分配库存数量

    @Column(name = "picked_quantity")
    private Integer pickedQuantity = 0; // 已拣选数量

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_order_id", insertable = false, updatable = false)
    private OutboundOrder outboundOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", insertable = false, updatable = false)
    private ProductSku productSku;
}
