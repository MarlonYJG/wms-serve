package com.bj.wms.mapper;

import com.bj.wms.dto.InboundOrderItemDTO;
import com.bj.wms.entity.InboundOrderItem;

public final class InboundOrderItemMapper {
    private InboundOrderItemMapper() {}

    public static InboundOrderItemDTO toDTO(InboundOrderItem entity) {
        if (entity == null) return null;
        InboundOrderItemDTO dto = new InboundOrderItemDTO();
        dto.setId(entity.getId());
        dto.setInboundOrderId(entity.getInboundOrderId());
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setExpectedQuantity(entity.getExpectedQuantity());
        dto.setReceivedQuantity(entity.getReceivedQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setBatchNo(entity.getBatchNo());
        dto.setProductionDate(entity.getProductionDate());
        dto.setExpiryDate(entity.getExpiryDate());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        return dto;
    }

    public static InboundOrderItem toEntity(InboundOrderItemDTO dto) {
        if (dto == null) return null;
        InboundOrderItem entity = new InboundOrderItem();
        entity.setId(dto.getId());
        entity.setInboundOrderId(dto.getInboundOrderId());
        entity.setProductSkuId(dto.getProductSkuId());
        entity.setExpectedQuantity(dto.getExpectedQuantity());
        entity.setReceivedQuantity(dto.getReceivedQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setBatchNo(dto.getBatchNo());
        entity.setProductionDate(dto.getProductionDate());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setCreatedTime(dto.getCreatedTime());
        entity.setUpdatedTime(dto.getUpdatedTime());
        return entity;
    }
}


