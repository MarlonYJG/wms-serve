package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 包装材料实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "packing_material")
public class PackingMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_code", unique = true, nullable = false, length = 50)
    private String materialCode;

    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;

    @Column(name = "material_type", nullable = false)
    private Integer materialType; // 1：纸箱，2：泡沫箱，3：塑料袋，4：木箱，5：其他

    @Column(name = "specification", length = 100)
    private String specification;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit", length = 20)
    private String unit = "个";

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "remark", length = 255)
    private String remark;
}
