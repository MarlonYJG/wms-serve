package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 入库费用表
 */
@Data
@Entity
@Table(name = "inbound_charge")
@EqualsAndHashCode(callSuper = true)
public class InboundCharge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 入库单ID
     */
    @Column(name = "inbound_order_id", nullable = false)
    private Long inboundOrderId;

    /**
     * 费用类型（关联费用字典）
     */
    @Column(name = "charge_type", nullable = false)
    private Long chargeType;

    /**
     * 费用金额
     */
    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * 税率（百分比）
     */
    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    /**
     * 币种
     */
    @Column(name = "currency", length = 10)
    private String currency = "CNY";

    /**
     * 备注
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 入库单信息（关联查询用）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_order_id", insertable = false, updatable = false)
    private InboundOrder inboundOrder;

    /**
     * 费用字典信息（关联查询用）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_type", insertable = false, updatable = false)
    private ChargeDict chargeDict;
}
