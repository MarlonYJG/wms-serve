package com.bj.wms.entity;

import com.bj.wms.entity.converter.QcStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "inbound_qc")
@EqualsAndHashCode(callSuper = true)
public class InboundQc extends BaseEntity {

    @NotNull
    @Column(name = "inbound_order_item_id", nullable = false)
    private Long inboundOrderItemId;

    @Convert(converter = QcStatusConverter.class)
    @Column(name = "status", nullable = false)
    private QcStatus status = QcStatus.PENDING;

    @Column(name = "qualified_quantity")
    private Integer qualifiedQuantity = 0;

    @Column(name = "unqualified_quantity")
    private Integer unqualifiedQuantity = 0;

    @Column(name = "remark")
    private String remark;
}


