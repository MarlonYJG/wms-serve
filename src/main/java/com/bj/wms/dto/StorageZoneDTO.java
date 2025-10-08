package com.bj.wms.dto;

import com.bj.wms.entity.ZoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StorageZoneDTO {
    private Long id;

    @NotNull(message = "所属仓库不能为空")
    private Long warehouseId;

    @NotBlank(message = "库区编码不能为空")
    @Size(max = 50)
    private String zoneCode;

    @NotBlank(message = "库区名称不能为空")
    @Size(max = 100)
    private String zoneName;

    @NotNull(message = "库区类型不能为空")
    private ZoneType zoneType;

    private BigDecimal capacity;

    private BigDecimal usedCapacity;

    private Boolean isEnabled;

    private Long createdTime;

    private Long updatedTime;
}


