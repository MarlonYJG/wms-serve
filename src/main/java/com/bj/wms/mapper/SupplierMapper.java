package com.bj.wms.mapper;

import com.bj.wms.dto.SupplierDTO;
import com.bj.wms.entity.Supplier;

public final class SupplierMapper {
    private SupplierMapper() {}

    public static SupplierDTO toDTO(Supplier entity) {
        if (entity == null) return null;
        SupplierDTO dto = new SupplierDTO();
        dto.setId(entity.getId());
        dto.setSupplierCode(entity.getSupplierCode());
        dto.setSupplierName(entity.getSupplierName());
        dto.setContactPerson(entity.getContactPerson());
        dto.setContactPhone(entity.getContactPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setRating(entity.getRating());
        dto.setIsEnabled(entity.getIsEnabled());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static Supplier toEntity(SupplierDTO dto) {
        if (dto == null) return null;
        Supplier entity = new Supplier();
        entity.setId(dto.getId());
        entity.setSupplierCode(dto.getSupplierCode());
        entity.setSupplierName(dto.getSupplierName());
        entity.setContactPerson(dto.getContactPerson());
        entity.setContactPhone(dto.getContactPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setRating(dto.getRating());
        entity.setIsEnabled(dto.getIsEnabled() == null ? Boolean.TRUE : dto.getIsEnabled());
        return entity;
    }
}


