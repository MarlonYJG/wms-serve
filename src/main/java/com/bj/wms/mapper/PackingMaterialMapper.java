package com.bj.wms.mapper;

import com.bj.wms.dto.PackingMaterialDTO;
import com.bj.wms.entity.PackingMaterial;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 包装材料映射器
 */
@Component
public class PackingMaterialMapper {

    public PackingMaterialDTO toDTO(PackingMaterial entity) {
        if (entity == null) {
            return null;
        }

        PackingMaterialDTO dto = new PackingMaterialDTO();
        dto.setId(entity.getId());
        dto.setMaterialCode(entity.getMaterialCode());
        dto.setMaterialName(entity.getMaterialName());
        dto.setMaterialType(entity.getMaterialType());
        dto.setMaterialTypeName(getMaterialTypeName(entity.getMaterialType()));
        dto.setSpecification(entity.getSpecification());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setUnit(entity.getUnit());
        dto.setIsEnabled(entity.getIsEnabled());
        dto.setRemark(entity.getRemark());

        return dto;
    }

    public List<PackingMaterialDTO> toDTOList(List<PackingMaterial> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String getMaterialTypeName(Integer materialType) {
        if (materialType == null) {
            return "";
        }
        return switch (materialType) {
            case 1 -> "纸箱";
            case 2 -> "泡沫箱";
            case 3 -> "塑料袋";
            case 4 -> "木箱";
            case 5 -> "其他";
            default -> "未知类型";
        };
    }
}
