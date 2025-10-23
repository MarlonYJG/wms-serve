package com.bj.wms.mapper;

import com.bj.wms.dto.PackingTaskDTO;
import com.bj.wms.entity.PackingTask;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 打包任务映射器
 */
@Component
public class PackingTaskMapper {

    public PackingTaskDTO toDTO(PackingTask entity) {
        if (entity == null) {
            return null;
        }

        PackingTaskDTO dto = new PackingTaskDTO();
        dto.setId(entity.getId());
        dto.setTaskNo(entity.getTaskNo());
        dto.setOutboundOrderId(entity.getOutboundOrderId());
        dto.setPackingMaterialId(entity.getPackingMaterialId());
        dto.setWeight(entity.getWeight());
        dto.setVolume(entity.getVolume());
        dto.setDimensions(entity.getDimensions());
        dto.setStatus(entity.getStatus());
        dto.setStatusName(getStatusName(entity.getStatus()));
        dto.setPackerId(entity.getPackerId());
        dto.setPackerName(entity.getPackerName());
        dto.setPackedTime(entity.getPackedTime());
        dto.setRemark(entity.getRemark());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());

        // 设置关联信息
        if (entity.getOutboundOrder() != null) {
            dto.setOutboundOrderNo(entity.getOutboundOrder().getOrderNo());
        }
        if (entity.getPackingMaterial() != null) {
            dto.setPackingMaterialName(entity.getPackingMaterial().getMaterialName());
            dto.setPackingMaterialCode(entity.getPackingMaterial().getMaterialCode());
        }

        return dto;
    }

    public List<PackingTaskDTO> toDTOList(List<PackingTask> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 1 -> "待打包";
            case 2 -> "打包中";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知状态";
        };
    }
}
