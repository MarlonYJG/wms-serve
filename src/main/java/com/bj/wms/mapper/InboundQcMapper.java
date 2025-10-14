package com.bj.wms.mapper;

import com.bj.wms.dto.InboundQcDTO;
import com.bj.wms.entity.InboundQc;

public final class InboundQcMapper {
    private InboundQcMapper() {}

    public static InboundQcDTO toDTO(InboundQc entity) {
        if (entity == null) return null;
        InboundQcDTO dto = new InboundQcDTO();
        dto.setId(entity.getId());
        dto.setInboundOrderItemId(entity.getInboundOrderItemId());
        dto.setStatus(entity.getStatus());
        dto.setQualifiedQuantity(entity.getQualifiedQuantity());
        dto.setUnqualifiedQuantity(entity.getUnqualifiedQuantity());
        dto.setRemark(entity.getRemark());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static InboundQc toEntity(InboundQcDTO dto) {
        if (dto == null) return null;
        InboundQc entity = new InboundQc();
        entity.setId(dto.getId());
        entity.setInboundOrderItemId(dto.getInboundOrderItemId());
        entity.setStatus(dto.getStatus());
        entity.setQualifiedQuantity(dto.getQualifiedQuantity());
        entity.setUnqualifiedQuantity(dto.getUnqualifiedQuantity());
        entity.setRemark(dto.getRemark());
        return entity;
    }
}


