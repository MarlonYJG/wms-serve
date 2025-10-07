package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库实体类
 * 
 * 仓库管理系统中的仓库信息
 */
@Data
// @Entity
// @Table(name = "warehouses")
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {

    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 50, message = "仓库编码长度不能超过50个字符")
    @Column(name = "warehouse_code", unique = true, nullable = false, length = 50)
    private String warehouseCode;

    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 200, message = "仓库名称长度不能超过200个字符")
    @Column(name = "warehouse_name", nullable = false, length = 200)
    private String warehouseName;

    @Size(max = 500, message = "仓库描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "仓库地址不能为空")
    @Size(max = 300, message = "仓库地址长度不能超过300个字符")
    @Column(name = "address", nullable = false, length = 300)
    private String address;

    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 100, message = "负责人长度不能超过100个字符")
    @Column(name = "manager", length = 100)
    private String manager;

    /**
     * 仓库状态
     * 0: 停用, 1: 启用
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 仓库容量（平方米）
     */
    @Column(name = "capacity")
    private Double capacity;

    /**
     * 已使用容量（平方米）
     */
    @Column(name = "used_capacity")
    private Double usedCapacity = 0.0;
}


