package com.bj.wms.mapper;

import com.bj.wms.dto.StorageLocationDTO;
import com.bj.wms.entity.StorageLocation;

public final class StorageLocationMapper {
    private StorageLocationMapper() {}

    public static StorageLocationDTO toDTO(StorageLocation entity) {
        if (entity == null) return null;
        StorageLocationDTO dto = new StorageLocationDTO();
        dto.setId(entity.getId());
        dto.setZoneId(entity.getZoneId());
        dto.setLocationCode(entity.getLocationCode());
        dto.setLocationName(entity.getLocationName());
        dto.setLocationType(entity.getLocationType());
        dto.setCapacity(entity.getCapacity());
        dto.setCurrentVolume(entity.getCurrentVolume());
        dto.setStatus(entity.getStatus());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static StorageLocation toEntity(StorageLocationDTO dto) {
        if (dto == null) return null;
        StorageLocation entity = new StorageLocation();
        entity.setId(dto.getId());
        entity.setZoneId(dto.getZoneId());
        entity.setLocationCode(dto.getLocationCode());
        entity.setLocationName(dto.getLocationName());
        entity.setLocationType(dto.getLocationType());
        entity.setCapacity(dto.getCapacity());
        entity.setCurrentVolume(dto.getCurrentVolume());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}


