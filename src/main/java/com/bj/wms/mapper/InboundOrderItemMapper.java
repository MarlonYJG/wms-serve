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
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
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
        return entity;
    }
}


