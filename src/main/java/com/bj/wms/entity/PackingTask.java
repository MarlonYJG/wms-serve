package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打包任务实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "packing_task")
public class PackingTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", unique = true, nullable = false, length = 50)
    private String taskNo;

    @Column(name = "outbound_order_id", nullable = false)
    private Long outboundOrderId;

    @Column(name = "packing_material_id")
    private Long packingMaterialId;

    @Column(name = "weight", precision = 12, scale = 4)
    private BigDecimal weight; // 重量(kg)

    @Column(name = "volume", precision = 12, scale = 6)
    private BigDecimal volume; // 体积(m³)

    @Column(name = "dimensions", length = 100)
    private String dimensions; // 尺寸(长x宽x高)

    @Column(name = "status")
    private Integer status = 1; // 1：待打包，2：打包中，3：已完成，4：已取消

    @Column(name = "packer_id")
    private Long packerId; // 打包员ID

    @Column(name = "packer_name", length = 50)
    private String packerName; // 打包员姓名

    @Column(name = "packed_time")
    private LocalDateTime packedTime; // 打包完成时间

    @Column(name = "remark", length = 500)
    private String remark; // 备注

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_order_id", insertable = false, updatable = false)
    private OutboundOrder outboundOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packing_material_id", insertable = false, updatable = false)
    private PackingMaterial packingMaterial;
}
