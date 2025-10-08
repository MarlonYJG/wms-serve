package com.bj.wms.mapper;

import com.bj.wms.dto.StorageZoneDTO;
import com.bj.wms.entity.StorageZone;

/**
 * StorageZone 映射器：实体与DTO相互转换
 */
public final class StorageZoneMapper {

    private StorageZoneMapper() {}

    public static StorageZoneDTO toDTO(StorageZone zone) {
        if (zone == null) {
            return null;
        }
        StorageZoneDTO dto = new StorageZoneDTO();
        dto.setId(zone.getId());
        dto.setWarehouseId(zone.getWarehouseId());
        dto.setZoneCode(zone.getZoneCode());
        dto.setZoneName(zone.getZoneName());
        dto.setZoneType(zone.getZoneType());
        dto.setCapacity(zone.getCapacity());
        dto.setUsedCapacity(zone.getUsedCapacity());
        dto.setIsEnabled(zone.getIsEnabled());
        if (zone.getCreatedTime() != null) {
            dto.setCreatedTime(zone.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (zone.getUpdatedTime() != null) {
            dto.setUpdatedTime(zone.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static StorageZone toEntity(StorageZoneDTO dto) {
        if (dto == null) {
            return null;
        }
        StorageZone zone = new StorageZone();
        zone.setId(dto.getId());
        zone.setWarehouseId(dto.getWarehouseId());
        zone.setZoneCode(dto.getZoneCode());
        zone.setZoneName(dto.getZoneName());
        zone.setZoneType(dto.getZoneType());
        zone.setCapacity(dto.getCapacity());
        zone.setUsedCapacity(dto.getUsedCapacity());
        zone.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : Boolean.TRUE);
        return zone;
    }
}


