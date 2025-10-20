package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "outbound_order")
public class OutboundOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false, length = 50)
    private String orderNo;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "status", nullable = false)
    private Integer status = 1; // 1：待处理，2：已分配库存，3：拣货中，4：已发货

    @Column(name = "customer_info", length = 500)
    private String customerInfo;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @OneToMany(mappedBy = "outboundOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OutboundOrderItem> items;

    @OneToMany(mappedBy = "outboundOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PickingTask> pickingTasks;
}
