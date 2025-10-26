package com.bj.wms.mapper;

import com.bj.wms.dto.PutawayTaskDTO;
import com.bj.wms.entity.InboundOrderItem;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.entity.PutawayTask;
import com.bj.wms.entity.StorageLocation;

import java.util.Map;

public final class PutawayTaskMapper {
    private PutawayTaskMapper() {}

    public static PutawayTaskDTO toDTO(PutawayTask entity) {
        if (entity == null) return null;
        PutawayTaskDTO dto = new PutawayTaskDTO();
        dto.setId(entity.getId());
        dto.setTaskNo(entity.getTaskNo());
        dto.setInboundOrderItemId(entity.getInboundOrderItemId());
        dto.setFromLocationId(entity.getFromLocationId());
        dto.setToLocationId(entity.getToLocationId());
        dto.setQuantity(entity.getQuantity());
        dto.setStatus(entity.getStatus());
        dto.setOperator(entity.getOperator());
        
        // 设置状态名称
        dto.setStatusName(getStatusName(entity.getStatus()));
        
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    /**
     * 带关联信息的DTO转换
     */
    public static PutawayTaskDTO toDTO(PutawayTask entity, 
                                     Map<Long, InboundOrderItem> itemMap,
                                     Map<Long, ProductSku> productSkuMap,
                                     Map<Long, StorageLocation> locationMap) {
        PutawayTaskDTO dto = toDTO(entity);
        if (dto == null) return null;
        
        // 设置入库明细关联信息
        InboundOrderItem item = itemMap.get(entity.getInboundOrderItemId());
        if (item != null) {
            dto.setProductSkuId(item.getProductSkuId());
            
            // 设置商品信息
            ProductSku productSku = productSkuMap.get(item.getProductSkuId());
            if (productSku != null) {
                dto.setSkuCode(productSku.getSkuCode());
                dto.setProductName(productSku.getSkuName());
            }
        }
        
        // 设置来源库位信息
        if (entity.getFromLocationId() != null) {
            StorageLocation fromLocation = locationMap.get(entity.getFromLocationId());
            if (fromLocation != null) {
                dto.setFromLocationCode(fromLocation.getLocationCode());
            }
        }
        
        // 设置目标库位信息
        if (entity.getToLocationId() != null) {
            StorageLocation toLocation = locationMap.get(entity.getToLocationId());
            if (toLocation != null) {
                dto.setToLocationCode(toLocation.getLocationCode());
            }
        }
        
        return dto;
    }
    
    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "待执行";
            case 2 -> "进行中";
            case 3 -> "已完成";
            default -> "未知";
        };
    }

    public static PutawayTask toEntity(PutawayTaskDTO dto) {
        if (dto == null) return null;
        PutawayTask entity = new PutawayTask();
        entity.setId(dto.getId());
        entity.setTaskNo(dto.getTaskNo());
        entity.setInboundOrderItemId(dto.getInboundOrderItemId());
        entity.setFromLocationId(dto.getFromLocationId());
        entity.setToLocationId(dto.getToLocationId());
        entity.setQuantity(dto.getQuantity());
        entity.setStatus(dto.getStatus());
        entity.setOperator(dto.getOperator());
        return entity;
    }
}


