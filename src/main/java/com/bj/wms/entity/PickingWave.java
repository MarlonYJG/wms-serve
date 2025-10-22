package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 拣货波次实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "picking_wave")
public class PickingWave extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wave_no", unique = true, nullable = false, length = 50)
    private String waveNo;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "status", nullable = false)
    private Integer status = 1; // 1：待执行，2：执行中，3：已完成

    @Column(name = "order_count")
    private Integer orderCount = 0; // 包含的订单数量

    @Column(name = "task_count")
    private Integer taskCount = 0; // 包含的任务数量

    @Column(name = "completed_task_count")
    private Integer completedTaskCount = 0; // 已完成的任务数量

    @Column(name = "started_time")
    private LocalDateTime startedTime; // 开始执行时间

    @Column(name = "completed_time")
    private LocalDateTime completedTime; // 完成时间

    @Column(name = "operator_id")
    private Long operatorId; // 操作员ID

    @Column(name = "operator_name", length = 50)
    private String operatorName; // 操作员姓名

    @Column(name = "remark", length = 500)
    private String remark; // 备注

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "pickingWave", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PickingTask> pickingTasks;
}
