package com.bj.wms.dto;

import com.bj.wms.entity.LocationStatus;
import com.bj.wms.entity.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StorageLocationDTO {
    private Long id;

    @NotNull(message = "所属库区不能为空")
    private Long zoneId;

    @NotBlank(message = "库位编码不能为空")
    @Size(max = 50)
    private String locationCode;

    @Size(max = 100)
    private String locationName;

    private LocationType locationType;

    private BigDecimal capacity;

    private BigDecimal currentVolume;

    private LocationStatus status;

    private Long createdTime;

    private Long updatedTime;
}


