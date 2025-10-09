package com.bj.wms.dto;

import com.bj.wms.entity.LocationStatus;
import com.bj.wms.entity.LocationType;
import lombok.Data;

@Data
public class StorageLocationQueryDTO {
    private Long zoneId;
    private LocationType locationType;
    private LocationStatus status;
    private String keyword;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDir;
}


