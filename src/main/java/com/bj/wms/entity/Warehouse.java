package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

/**
 * 仓库实体类
 * 
 * 仓库管理系统中的仓库信息
 */
@Data
@Entity
@Table(name = "warehouse")
@EqualsAndHashCode(callSuper = true)
public class Warehouse extends BaseEntity {

    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 50, message = "仓库编码长度不能超过50个字符")
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 200, message = "仓库名称长度不能超过200个字符")
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Size(max = 300, message = "仓库地址长度不能超过300个字符")
    @Column(name = "address", length = 300)
    private String address;

    @Size(max = 50, message = "联系人长度不能超过50个字符")
    @Column(name = "contact_person", length = 50)
    private String contactPerson;

    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    /**
     * 仓库状态
     */
    @Column(name = "is_enabled", nullable = false, columnDefinition = "BIT")
    private Boolean isEnabled = true;

    /**
     * 仓库容量（平方米）
     */
    @Column(name = "total_capacity")
    private BigDecimal totalCapacity;

    /**
     * 已使用容量（平方米）
     */
    @Column(name = "used_capacity")
    private BigDecimal usedCapacity;
}


