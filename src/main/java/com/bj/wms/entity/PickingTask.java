package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拣货任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "picking_task")
public class PickingTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", unique = true, nullable = false, length = 50)
    private String taskNo;

    @Column(name = "wave_no", length = 50)
    private String waveNo; // 波次号

    @Column(name = "picking_wave_id")
    private Long pickingWaveId; // 波次ID

    @Column(name = "outbound_order_id", nullable = false)
    private Long outboundOrderId;

    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @Column(name = "from_location_id", nullable = false)
    private Long fromLocationId; // 拣货库位

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // 需拣选数量

    @Column(name = "status")
    private Integer status = 1; // 1：待拣选，2：部分完成，3：已完成

    @Column(name = "picked_quantity")
    private Integer pickedQuantity = 0; // 已拣选数量

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_order_id", insertable = false, updatable = false)
    private OutboundOrder outboundOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", insertable = false, updatable = false)
    private ProductSku productSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id", insertable = false, updatable = false)
    private StorageLocation fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picking_wave_id", insertable = false, updatable = false)
    private PickingWave pickingWave;
}
