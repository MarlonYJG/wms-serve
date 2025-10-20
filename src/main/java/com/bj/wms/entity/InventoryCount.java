package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "inventory_count")
@EqualsAndHashCode(callSuper = true)
public class InventoryCount extends BaseEntity {

    @Column(name = "count_no", nullable = false, unique = true, length = 50)
    private String countNo;

    @NotNull
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    /** 1草稿 2进行中 3已提交 4已完成 */
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status = 1;

    @Column(name = "remark")
    private String remark;
}


