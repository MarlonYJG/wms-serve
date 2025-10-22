package com.bj.wms.mapper;

import com.bj.wms.dto.PickingWaveDTO;
import com.bj.wms.entity.PickingWave;
import org.springframework.stereotype.Component;

/**
 * 拣货波次映射器
 */
@Component
public class PickingWaveMapper {

    public static PickingWaveDTO toDTO(PickingWave entity) {
        if (entity == null) {
            return null;
        }

        PickingWaveDTO dto = new PickingWaveDTO();
        dto.setId(entity.getId());
        dto.setWaveNo(entity.getWaveNo());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setStatus(entity.getStatus());
        dto.setOrderCount(entity.getOrderCount());
        dto.setTaskCount(entity.getTaskCount());
        dto.setCompletedTaskCount(entity.getCompletedTaskCount());
        dto.setStartedTime(entity.getStartedTime());
        dto.setCompletedTime(entity.getCompletedTime());
        dto.setOperatorId(entity.getOperatorId());
        dto.setOperatorName(entity.getOperatorName());
        dto.setRemark(entity.getRemark());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());

        // 设置关联信息
        if (entity.getWarehouse() != null) {
            dto.setWarehouseName(entity.getWarehouse().getName());
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
            case 1 -> "待执行";
            case 2 -> "执行中";
            case 3 -> "已完成";
            default -> "未知";
        };
    }
}
