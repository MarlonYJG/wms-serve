package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 复核任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "review_task")
public class ReviewTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", unique = true, nullable = false, length = 50)
    private String taskNo;

    @Column(name = "outbound_order_id", nullable = false)
    private Long outboundOrderId;

    @Column(name = "product_sku_id", nullable = false)
    private Long productSkuId;

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity; // 预期数量

    @Column(name = "actual_quantity")
    private Integer actualQuantity = 0; // 实际数量

    @Column(name = "status")
    private Integer status = 1; // 1：待复核，2：复核中，3：复核完成，4：复核异常

    @Column(name = "reviewer_id")
    private Long reviewerId; // 复核员ID

    @Column(name = "reviewer_name", length = 50)
    private String reviewerName; // 复核员姓名

    @Column(name = "review_time")
    private LocalDateTime reviewTime; // 复核时间

    @Column(name = "remark", length = 500)
    private String remark; // 备注

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_order_id", insertable = false, updatable = false)
    private OutboundOrder outboundOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", insertable = false, updatable = false)
    private ProductSku productSku;
}
