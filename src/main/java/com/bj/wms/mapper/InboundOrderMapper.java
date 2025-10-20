package com.bj.wms.mapper;

import com.bj.wms.dto.*;
import com.bj.wms.entity.InboundOrder;
import com.bj.wms.entity.InboundOrderItem;

import java.util.List;
import java.util.stream.Collectors;

public final class InboundOrderMapper {
    private InboundOrderMapper() {}

    public static InboundOrderDTO toDTO(InboundOrder entity) {
        return toDTO(entity, false);
    }
    
    public static InboundOrderDTO toDTO(InboundOrder entity, boolean includeItems) {
        if (entity == null) return null;
        InboundOrderDTO dto = new InboundOrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setSupplierId(entity.getSupplierId());
        dto.setStatus(entity.getStatus());
        dto.setStatusName(entity.getStatus() != null ? entity.getStatus().getDescription() : null);
        dto.setTotalExpectedQuantity(entity.getTotalExpectedQuantity());
        dto.setTotalReceivedQuantity(entity.getTotalReceivedQuantity());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        
        // 只有在需要时才转换明细（避免懒加载问题）
        if (includeItems && entity.getOrderItems() != null) {
            dto.setItems(entity.getOrderItems().stream()
                .map(InboundOrderMapper::toItemDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public static InboundOrderItemDTO toItemDTO(InboundOrderItem entity) {
        if (entity == null) return null;
        InboundOrderItemDTO dto = new InboundOrderItemDTO();
        dto.setId(entity.getId());
        dto.setInboundOrderId(entity.getInboundOrderId());
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setExpectedQuantity(entity.getExpectedQuantity());
        dto.setReceivedQuantity(entity.getReceivedQuantity());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        
        // 如果有关联的 ProductSku，设置相关信息
        if (entity.getProductSku() != null) {
            dto.setProductSkuCode(entity.getProductSku().getSkuCode());
            dto.setProductSkuName(entity.getProductSku().getSkuName());
        }
        
        return dto;
    }

    public static InboundOrder toEntity(InboundOrderCreateRequest request) {
        if (request == null) return null;
        InboundOrder entity = new InboundOrder();
        entity.setWarehouseId(request.getWarehouseId());
        entity.setSupplierId(request.getSupplierId());
        entity.setStatus(com.bj.wms.entity.InboundStatus.PENDING);
        
        // 转换明细
        if (request.getItems() != null) {
            List<InboundOrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    InboundOrderItem item = new InboundOrderItem();
                    item.setProductSkuId(itemRequest.getProductSkuId());
                    item.setExpectedQuantity(itemRequest.getExpectedQuantity());
                    item.setUnitPrice(itemRequest.getUnitPrice());
                    item.setBatchNo(itemRequest.getBatchNo());
                    item.setProductionDate(itemRequest.getProductionDate());
                    item.setExpiryDate(itemRequest.getExpiryDate());
                    item.setInboundOrder(entity);
                    return item;
                })
                .collect(Collectors.toList());
            entity.setOrderItems(items);
            
            // 计算总预期数量
            int totalExpected = items.stream()
                .mapToInt(InboundOrderItem::getExpectedQuantity)
                .sum();
            entity.setTotalExpectedQuantity(totalExpected);
        }
        
        return entity;
    }
}