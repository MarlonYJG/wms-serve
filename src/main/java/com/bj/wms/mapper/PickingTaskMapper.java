package com.bj.wms.mapper;

import com.bj.wms.dto.PickingTaskDTO;
import com.bj.wms.entity.PickingTask;
import org.springframework.stereotype.Component;

/**
 * 拣货任务映射器
 */
@Component
public class PickingTaskMapper {

    public static PickingTaskDTO toDTO(PickingTask entity) {
        if (entity == null) {
            return null;
        }

        PickingTaskDTO dto = new PickingTaskDTO();
        dto.setId(entity.getId());
        dto.setTaskNo(entity.getTaskNo());
        dto.setWaveNo(entity.getWaveNo());
        dto.setOutboundOrderId(entity.getOutboundOrderId());
        dto.setProductSkuId(entity.getProductSkuId());
        dto.setFromLocationId(entity.getFromLocationId());
        dto.setQuantity(entity.getQuantity());
        dto.setStatus(entity.getStatus());
        dto.setPickedQuantity(entity.getPickedQuantity());
        dto.setCreatedTime(entity.getCreatedTime());

        // 设置关联信息
        if (entity.getOutboundOrder() != null) {
            dto.setOutboundOrderNo(entity.getOutboundOrder().getOrderNo());
        }
        if (entity.getProductSku() != null) {
            dto.setProductName(entity.getProductSku().getSkuName());
            dto.setSkuCode(entity.getProductSku().getSkuCode());
        }
        if (entity.getFromLocation() != null) {
            dto.setFromLocationCode(entity.getFromLocation().getLocationCode());
        }

        // 设置状态名称
        dto.setStatusName(getStatusName(entity.getStatus()));

        return dto;
    }

    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "待拣选";
            case 2 -> "部分完成";
            case 3 -> "已完成";
            default -> "未知";
        };
    }
}
