package com.bj.wms.mapper;

import com.bj.wms.dto.InventoryDTO;
import com.bj.wms.entity.Inventory;

public final class InventoryMapper {
    private InventoryMapper() {}

    public static InventoryDTO toDTO(Inventory entity, String warehouseName, String locationCode, String skuCode, String productName) {
        if (entity == null) return null;
        InventoryDTO dto = new InventoryDTO();
        dto.setId(entity.getId());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setWarehouseName(warehouseName);
        dto.setLocationId(entity.getLocationId());
        dto.setLocationCode(locationCode);
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setSkuCode(skuCode);
        dto.setProductName(productName);
        dto.setBatchNo(entity.getBatchNo());
        dto.setProductionDate(entity.getProductionDate() == null ? null : entity.getProductionDate().toString());
        dto.setExpiryDate(entity.getExpiryDate() == null ? null : entity.getExpiryDate().toString());
        dto.setQuantity(entity.getQuantity());
        dto.setLockedQuantity(entity.getLockedQuantity());
        int available = (entity.getQuantity() == null ? 0 : entity.getQuantity()) - (entity.getLockedQuantity() == null ? 0 : entity.getLockedQuantity());
        dto.setAvailableQuantity(Math.max(available, 0));
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }
}


