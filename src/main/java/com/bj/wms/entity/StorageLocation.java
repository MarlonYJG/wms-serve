package com.bj.wms.entity;

import com.bj.wms.entity.converter.LocationStatusConverter;
import com.bj.wms.entity.converter.LocationTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库位实体
 */
@Data
@Entity
@Table(name = "storage_location")
@EqualsAndHashCode(callSuper = true)
public class StorageLocation extends BaseEntity {

    /** 所属库区ID */
    @NotNull(message = "所属库区不能为空")
    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    /** 库位编码（全局唯一） */
    @NotBlank(message = "库位编码不能为空")
    @Size(max = 50, message = "库位编码长度不能超过50个字符")
    @Column(name = "location_code", nullable = false, length = 50, unique = true)
    private String locationCode;

    /** 库位名称 */
    @Size(max = 100, message = "库位名称长度不能超过100个字符")
    @Column(name = "location_name", length = 100)
    private String locationName;

    /** 库位类型（DB保存业务编码） */
    @Convert(converter = LocationTypeConverter.class)
    @Column(name = "location_type")
    private LocationType locationType;

    /** 设计容量 */
    @Column(name = "capacity")
    private BigDecimal capacity;

    /** 当前占用容量 */
    @Column(name = "current_volume")
    private BigDecimal currentVolume = BigDecimal.ZERO;

    /** 状态 */
    @Convert(converter = LocationStatusConverter.class)
    @Column(name = "status")
    private LocationStatus status = LocationStatus.AVAILABLE;
}


