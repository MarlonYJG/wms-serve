package com.bj.wms.mapper;

import com.bj.wms.dto.WarehouseDTO;
import com.bj.wms.entity.Warehouse;

/**
 * Warehouse 映射器：实体与DTO相互转换
 */
public final class WarehouseMapper {

    private WarehouseMapper() {}

    public static WarehouseDTO toDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setCode(warehouse.getCode());
        dto.setName(warehouse.getName());
        dto.setAddress(warehouse.getAddress());
        dto.setContactPerson(warehouse.getContactPerson());
        dto.setContactPhone(warehouse.getContactPhone());
        dto.setIsEnabled(warehouse.getIsEnabled());
        dto.setTotalCapacity(warehouse.getTotalCapacity());
        dto.setUsedCapacity(warehouse.getUsedCapacity());
        dto.setCreatedBy(warehouse.getCreatedBy());
        dto.setUpdatedBy(warehouse.getUpdatedBy());
        if (warehouse.getCreatedTime() != null) {
            dto.setCreatedTime(warehouse.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (warehouse.getUpdatedTime() != null) {
            dto.setUpdatedTime(warehouse.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static Warehouse toEntity(WarehouseDTO dto) {
        if (dto == null) {
            return null;
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setId(dto.getId());
        warehouse.setCode(dto.getCode());
        warehouse.setName(dto.getName());
        warehouse.setAddress(dto.getAddress());
        warehouse.setContactPerson(dto.getContactPerson());
        warehouse.setContactPhone(dto.getContactPhone());
        warehouse.setIsEnabled(dto.getIsEnabled());
        warehouse.setTotalCapacity(dto.getTotalCapacity());
        warehouse.setUsedCapacity(dto.getUsedCapacity());
        warehouse.setCreatedBy(dto.getCreatedBy());
        warehouse.setUpdatedBy(dto.getUpdatedBy());
        return warehouse;
    }
}


