package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 费用项字典表
 */
@Data
@Entity
@Table(name = "charge_dict")
@EqualsAndHashCode(callSuper = true)
public class ChargeDict extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 费用编码
     */
    @Column(name = "charge_code", unique = true, nullable = false, length = 50)
    private String chargeCode;

    /**
     * 费用名称
     */
    @Column(name = "charge_name", nullable = false, length = 100)
    private String chargeName;

    /**
     * 默认税率（百分比）
     */
    @Column(name = "default_tax_rate", precision = 5, scale = 2)
    private BigDecimal defaultTaxRate;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled", columnDefinition = "BIT")
    private Boolean isEnabled = true;

    /**
     * 备注
     */
    @Column(name = "remark", length = 255)
    private String remark;
}
