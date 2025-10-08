package com.bj.wms.util;

import com.bj.wms.dto.WarehouseDTO;
import com.bj.wms.entity.Warehouse;
import org.springframework.beans.BeanUtils;

import java.time.ZoneOffset;

/**
 * DTO转换工具类
 * 
 * 用于实体类与DTO之间的转换
 */
public class DTOConverter {
    
    /**
     * 将Warehouse实体转换为WarehouseDTO
     */
    public static WarehouseDTO toWarehouseDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        
        WarehouseDTO dto = new WarehouseDTO();
        
        // 复制基本字段
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
        
        // 转换时间字段为时间戳（毫秒）- 使用系统默认时区
        if (warehouse.getCreatedTime() != null) {
            dto.setCreatedTime(warehouse.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (warehouse.getUpdatedTime() != null) {
            dto.setUpdatedTime(warehouse.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        
        return dto;
    }
    
    /**
     * 将WarehouseDTO转换为Warehouse实体
     */
    public static Warehouse toWarehouse(WarehouseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Warehouse warehouse = new Warehouse();
        
        // 复制基本字段
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
        
        // 时间字段由JPA自动管理，不需要手动转换
        
        return warehouse;
    }
}