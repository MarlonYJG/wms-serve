package com.bj.wms.mapper;

import com.bj.wms.dto.ProductSkuDTO;
import com.bj.wms.entity.ProductSku;

public final class ProductSkuMapper {
    private ProductSkuMapper() {}

    public static ProductSkuDTO toDTO(ProductSku entity) {
        if (entity == null) return null;
        ProductSkuDTO dto = new ProductSkuDTO();
        dto.setId(entity.getId());
        dto.setSkuCode(entity.getSkuCode());
        dto.setSkuName(entity.getSkuName());
        dto.setSpecification(entity.getSpecification());
        dto.setBrand(entity.getBrand());
        dto.setCategoryId(entity.getCategoryId());
        dto.setSupplierId(entity.getSupplierId());
        dto.setBarcode(entity.getBarcode());
        dto.setWeight(entity.getWeight());
        dto.setVolume(entity.getVolume());
        dto.setIsBatchManaged(entity.getIsBatchManaged());
        dto.setIsExpiryManaged(entity.getIsExpiryManaged());
        dto.setShelfLifeDays(entity.getShelfLifeDays());
        dto.setSafetyStock(entity.getSafetyStock());
        dto.setIsEnabled(entity.getIsEnabled());
        // 价格字段映射
        dto.setPurchasePrice(entity.getPurchasePrice());
        dto.setCostPrice(entity.getCostPrice());
        dto.setSalePrice(entity.getSalePrice());
        dto.setRetailPrice(entity.getRetailPrice());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static ProductSku toEntity(ProductSkuDTO dto) {
        if (dto == null) return null;
        ProductSku entity = new ProductSku();
        entity.setId(dto.getId());
        entity.setSkuCode(dto.getSkuCode());
        entity.setSkuName(dto.getSkuName());
        entity.setSpecification(dto.getSpecification());
        entity.setBrand(dto.getBrand());
        entity.setCategoryId(dto.getCategoryId());
        entity.setSupplierId(dto.getSupplierId());
        entity.setBarcode(dto.getBarcode());
        entity.setWeight(dto.getWeight());
        entity.setVolume(dto.getVolume());
        entity.setIsBatchManaged(dto.getIsBatchManaged());
        entity.setIsExpiryManaged(dto.getIsExpiryManaged());
        entity.setShelfLifeDays(dto.getShelfLifeDays());
        entity.setSafetyStock(dto.getSafetyStock());
        entity.setIsEnabled(dto.getIsEnabled() == null ? Boolean.TRUE : dto.getIsEnabled());
        // 价格字段映射
        entity.setPurchasePrice(dto.getPurchasePrice());
        entity.setCostPrice(dto.getCostPrice());
        entity.setSalePrice(dto.getSalePrice());
        entity.setRetailPrice(dto.getRetailPrice());
        return entity;
    }
}


