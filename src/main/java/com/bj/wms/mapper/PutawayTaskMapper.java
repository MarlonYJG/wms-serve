package com.bj.wms.mapper;

import com.bj.wms.dto.PutawayTaskDTO;
import com.bj.wms.entity.PutawayTask;

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
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
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


