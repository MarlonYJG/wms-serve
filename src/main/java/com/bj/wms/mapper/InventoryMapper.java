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
            dto.setCreatedTime(entity.getCreatedTime().toString());
        }
        return dto;
    }

    public static InventoryDTO toDTO(Inventory entity, String warehouseName, String locationCode, 
                                   String skuCode, String productName, String zoneCode, String zoneName, 
                                   String zoneType, String zoneTypeName) {
        InventoryDTO dto = toDTO(entity, warehouseName, locationCode, skuCode, productName);
        if (dto == null) return null;
        
        dto.setZoneCode(zoneCode);
        dto.setZoneName(zoneName);
        dto.setZoneType(zoneType);
        dto.setZoneTypeName(zoneTypeName);
        
        return dto;
    }

    public static InventoryDTO toDTO(Inventory entity, String warehouseName, String locationCode, 
                                   String skuCode, String productName, String zoneCode, String zoneName, 
                                   String zoneType, String zoneTypeName, java.math.BigDecimal purchasePrice,
                                   java.math.BigDecimal costPrice, java.math.BigDecimal salePrice,
                                   java.math.BigDecimal retailPrice, java.math.BigDecimal inboundCharges,
                                   java.math.BigDecimal unitInboundCharges) {
        InventoryDTO dto = toDTO(entity, warehouseName, locationCode, skuCode, productName, 
                                zoneCode, zoneName, zoneType, zoneTypeName);
        if (dto == null) return null;
        
        // 设置价格信息
        dto.setPurchasePrice(purchasePrice);
        dto.setCostPrice(costPrice);
        dto.setSalePrice(salePrice);
        dto.setRetailPrice(retailPrice);
        
        // 设置费用信息
        dto.setInboundCharges(inboundCharges);
        dto.setUnitInboundCharges(unitInboundCharges);
        
        // 计算总成本
        if (purchasePrice != null && unitInboundCharges != null) {
            dto.setUnitTotalCost(purchasePrice.add(unitInboundCharges));
            if (entity.getQuantity() != null) {
                dto.setTotalCost(dto.getUnitTotalCost().multiply(java.math.BigDecimal.valueOf(entity.getQuantity())));
            }
        } else if (purchasePrice != null) {
            dto.setUnitTotalCost(purchasePrice);
            if (entity.getQuantity() != null) {
                dto.setTotalCost(purchasePrice.multiply(java.math.BigDecimal.valueOf(entity.getQuantity())));
            }
        }
        
        return dto;
    }
}


