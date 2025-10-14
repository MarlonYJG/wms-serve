package com.bj.wms.mapper;

import com.bj.wms.dto.InboundOrderDTO;
import com.bj.wms.entity.InboundOrder;

public final class InboundOrderMapper {
    private InboundOrderMapper() {}

    public static InboundOrderDTO toDTO(InboundOrder entity) {
        if (entity == null) return null;
        InboundOrderDTO dto = new InboundOrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setSupplierId(entity.getSupplierId());
        dto.setStatus(entity.getStatus());
        dto.setTotalExpectedQuantity(entity.getTotalExpectedQuantity());
        dto.setTotalReceivedQuantity(entity.getTotalReceivedQuantity());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static InboundOrder toEntity(InboundOrderDTO dto) {
        if (dto == null) return null;
        InboundOrder entity = new InboundOrder();
        entity.setId(dto.getId());
        entity.setOrderNo(dto.getOrderNo());
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setSupplierId(dto.getSupplierId());
        entity.setStatus(dto.getStatus());
        entity.setTotalExpectedQuantity(dto.getTotalExpectedQuantity());
        entity.setTotalReceivedQuantity(dto.getTotalReceivedQuantity());
        return entity;
    }
}


