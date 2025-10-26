package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "putaway_task")
@EqualsAndHashCode(callSuper = true)
public class PutawayTask extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "task_no", nullable = false, unique = true, length = 50)
    private String taskNo;

    @NotNull
    @Column(name = "inbound_order_item_id", nullable = false)
    private Long inboundOrderItemId;

    @Column(name = "from_location_id")
    private Long fromLocationId;

    @Column(name = "to_location_id")
    private Long toLocationId;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 1：待执行，2：进行中，3：已完成
     */
    @Column(name = "status")
    private Integer status = 1;

    @Column(name = "operator")
    private Integer operator;
}


