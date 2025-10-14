package com.bj.wms.mapper;

import com.bj.wms.dto.InboundAppointmentDTO;
import com.bj.wms.entity.InboundAppointment;

public final class InboundAppointmentMapper {
    private InboundAppointmentMapper() {}

    public static InboundAppointmentDTO toDTO(InboundAppointment entity) {
        if (entity == null) return null;
        InboundAppointmentDTO dto = new InboundAppointmentDTO();
        dto.setId(entity.getId());
        dto.setAppointmentNo(entity.getAppointmentNo());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setSupplierId(entity.getSupplierId());
        dto.setExpectedArrivalTime(entity.getExpectedArrivalTime());
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getRemark());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static InboundAppointment toEntity(InboundAppointmentDTO dto) {
        if (dto == null) return null;
        InboundAppointment entity = new InboundAppointment();
        entity.setId(dto.getId());
        entity.setAppointmentNo(dto.getAppointmentNo());
        entity.setWarehouseId(dto.getWarehouseId());
        entity.setSupplierId(dto.getSupplierId());
        entity.setExpectedArrivalTime(dto.getExpectedArrivalTime());
        entity.setStatus(dto.getStatus());
        entity.setRemark(dto.getRemark());
        return entity;
    }
}


