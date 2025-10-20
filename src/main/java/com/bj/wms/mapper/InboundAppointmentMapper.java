package com.bj.wms.mapper;

import com.bj.wms.dto.*;
import com.bj.wms.entity.InboundAppointment;
import com.bj.wms.entity.InboundAppointmentItem;

import java.util.List;
import java.util.stream.Collectors;

public final class InboundAppointmentMapper {
    private InboundAppointmentMapper() {}

    public static InboundAppointmentDTO toDTO(InboundAppointment entity) {
        return toDTO(entity, false);
    }
    
    public static InboundAppointmentDTO toDTO(InboundAppointment entity, boolean includeItems) {
        if (entity == null) return null;
        InboundAppointmentDTO dto = new InboundAppointmentDTO();
        dto.setId(entity.getId());
        dto.setAppointmentNo(entity.getAppointmentNo());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setSupplierId(entity.getSupplierId());
        dto.setAppointmentDate(entity.getAppointmentDate());
        dto.setAppointmentTimeStart(entity.getAppointmentTimeStart());
        dto.setAppointmentTimeEnd(entity.getAppointmentTimeEnd());
        dto.setStatus(entity.getStatus());
        dto.setStatusName(entity.getStatus() != null ? entity.getStatus().getDescription() : null);
        dto.setTotalExpectedQuantity(entity.getTotalExpectedQuantity());
        dto.setSpecialRequirements(entity.getSpecialRequirements());
        dto.setApprovedBy(entity.getApprovedBy());
        dto.setApprovedTime(entity.getApprovedTime());
        dto.setRemark(entity.getRemark());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        
        // 只有在需要时才转换明细（避免懒加载问题）
        if (includeItems && entity.getAppointmentItems() != null) {
            dto.setAppointmentItems(entity.getAppointmentItems().stream()
                .map(InboundAppointmentMapper::toItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public static InboundAppointmentItemDTO toItemDTO(InboundAppointmentItem entity) {
        if (entity == null) return null;
        InboundAppointmentItemDTO dto = new InboundAppointmentItemDTO();
        dto.setId(entity.getId());
        dto.setAppointmentId(entity.getAppointmentId());
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setExpectedQuantity(entity.getExpectedQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setBatchNo(entity.getBatchNo());
        dto.setProductionDate(entity.getProductionDate());
        dto.setExpiryDate(entity.getExpiryDate());
        
        // 设置商品信息
        if (entity.getProductSku() != null) {
            dto.setProductName(entity.getProductSku().getSkuName());
            dto.setSkuCode(entity.getProductSku().getSkuCode());
        }
        
        return dto;
    }

    public static InboundAppointment toEntity(InboundAppointmentCreateRequest request) {
        if (request == null) return null;
        InboundAppointment entity = new InboundAppointment();
        entity.setWarehouseId(request.getWarehouseId());
        entity.setSupplierId(request.getSupplierId());
        entity.setAppointmentDate(request.getAppointmentDate());
        entity.setAppointmentTimeStart(request.getAppointmentTimeStart());
        entity.setAppointmentTimeEnd(request.getAppointmentTimeEnd());
        entity.setSpecialRequirements(request.getSpecialRequirements());
        entity.setStatus(com.bj.wms.entity.AppointmentStatus.PENDING);
        
        // 转换明细
        if (request.getAppointmentItems() != null) {
            List<InboundAppointmentItem> items = request.getAppointmentItems().stream()
                .map(itemRequest -> {
                    InboundAppointmentItem item = new InboundAppointmentItem();
                    item.setProductSkuId(itemRequest.getProductSkuId());
                    item.setExpectedQuantity(itemRequest.getExpectedQuantity());
                    item.setUnitPrice(itemRequest.getUnitPrice());
                    item.setBatchNo(itemRequest.getBatchNo());
                    item.setProductionDate(itemRequest.getProductionDate());
                    item.setExpiryDate(itemRequest.getExpiryDate());
                    item.setInboundAppointment(entity);
                    return item;
                })
                .collect(Collectors.toList());
            entity.setAppointmentItems(items);
        }
        
        return entity;
    }
}


