package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 结算明细表
 */
@Data
@Entity
@Table(name = "settlement_item")
@EqualsAndHashCode(callSuper = true)
public class SettlementItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 结算单ID
     */
    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;

    /**
     * 出库单ID
     */
    @Column(name = "outbound_order_id", nullable = false)
    private Long outboundOrderId;

    /**
     * 商品金额
     */
    @Column(name = "amount_goods", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountGoods = BigDecimal.ZERO;

    /**
     * 费用金额
     */
    @Column(name = "amount_charges", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountCharges = BigDecimal.ZERO;

    /**
     * 总金额
     */
    @Column(name = "amount_total", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountTotal = BigDecimal.ZERO;

    /**
     * 备注
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 结算单信息（关联查询用）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", insertable = false, updatable = false)
    private Settlement settlement;

    /**
     * 出库单信息（关联查询用）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_order_id", insertable = false, updatable = false)
    private OutboundOrder outboundOrder;
}
