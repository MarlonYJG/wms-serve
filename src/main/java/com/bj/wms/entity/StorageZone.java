package com.bj.wms.entity;

import jakarta.persistence.*;
import com.bj.wms.entity.converter.ZoneTypeConverter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库区实体
 */
@Data
@Entity
@Table(name = "storage_zone")
@EqualsAndHashCode(callSuper = true)
public class StorageZone extends BaseEntity {

    /** 所属仓库ID */
    @NotNull(message = "所属仓库不能为空")
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    /** 库区编码（同一系统唯一） */
    @NotBlank(message = "库区编码不能为空")
    @Size(max = 50, message = "库区编码长度不能超过50个字符")
    @Column(name = "zone_code", nullable = false, length = 50, unique = true)
    private String zoneCode;

    /** 库区名称 */
    @NotBlank(message = "库区名称不能为空")
    @Size(max = 100, message = "库区名称长度不能超过100个字符")
    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName;

    /** 库区类型（按业务编码 1..6 存储） */
    @NotNull(message = "库区类型不能为空")
    @Convert(converter = ZoneTypeConverter.class)
    @Column(name = "zone_type", nullable = false)
    private ZoneType zoneType;

    /** 容量 */
    @Column(name = "capacity")
    private BigDecimal capacity;

    /** 已用容量 */
    @Column(name = "used_capacity")
    private BigDecimal usedCapacity;

    /** 是否启用 */
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;
}


