package com.bj.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 结算主表
 */
@Data
@Entity
@Table(name = "settlement")
@EqualsAndHashCode(callSuper = true)
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 结算单号
     */
    @Column(name = "settlement_no", unique = true, nullable = false, length = 50)
    private String settlementNo;

    /**
     * 客户ID
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 结算周期开始时间
     */
    @Column(name = "period_start")
    private LocalDateTime periodStart;

    /**
     * 结算周期结束时间
     */
    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    /**
     * 状态：1草稿 2待审核 3已审核 4已结清 5已作废
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 币种
     */
    @Column(name = "currency", length = 10)
    private String currency = "CNY";

    /**
     * 商品金额合计
     */
    @Column(name = "amount_goods", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountGoods = BigDecimal.ZERO;

    /**
     * 费用金额合计
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
     * 客户信息（关联查询用）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;
}
