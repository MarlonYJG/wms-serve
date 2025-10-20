package com.bj.wms.mapper;

import com.bj.wms.dto.OutboundOrderDTO;
import com.bj.wms.dto.OutboundOrderItemDTO;
import com.bj.wms.entity.OutboundOrder;
import com.bj.wms.entity.OutboundOrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 出库单映射器
 */
@Component
public class OutboundOrderMapper {

    public static OutboundOrderDTO toDTO(OutboundOrder entity) {
        if (entity == null) {
            return null;
        }

        OutboundOrderDTO dto = new OutboundOrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setCustomerId(entity.getCustomerId());
        dto.setStatus(entity.getStatus());
        dto.setCustomerInfo(entity.getCustomerInfo());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());

        // 设置关联信息
        if (entity.getWarehouse() != null) {
            dto.setWarehouseName(entity.getWarehouse().getName());
        }
        if (entity.getCustomer() != null) {
            dto.setCustomerName(entity.getCustomer().getCustomerName());
        }

        // 设置状态名称
        dto.setStatusName(getStatusName(entity.getStatus()));

        // 设置明细
        if (entity.getItems() != null) {
            dto.setItems(entity.getItems().stream()
                    .map(OutboundOrderMapper::toItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static OutboundOrderItemDTO toItemDTO(OutboundOrderItem entity) {
        if (entity == null) {
            return null;
        }

        OutboundOrderItemDTO dto = new OutboundOrderItemDTO();
        dto.setId(entity.getId());
        dto.setOutboundOrderId(entity.getOutboundOrderId());
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setQuantity(entity.getQuantity());
        dto.setAllocatedQuantity(entity.getAllocatedQuantity());
        dto.setPickedQuantity(entity.getPickedQuantity());
        dto.setCreatedTime(entity.getCreatedTime());

        // 设置商品信息
        if (entity.getProductSku() != null) {
            dto.setProductName(entity.getProductSku().getSkuName());
            dto.setSkuCode(entity.getProductSku().getSkuCode());
        }

        return dto;
    }

    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "待处理";
            case 2 -> "已分配库存";
            case 3 -> "拣货中";
            case 4 -> "已发货";
            default -> "未知";
        };
    }
}
