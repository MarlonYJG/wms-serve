package com.bj.wms.service;

import com.bj.wms.dto.PackingMaterialDTO;
import com.bj.wms.entity.PackingMaterial;
import com.bj.wms.mapper.PackingMaterialMapper;
import com.bj.wms.repository.PackingMaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 包装材料服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PackingMaterialService {

    private final PackingMaterialRepository packingMaterialRepository;
    private final PackingMaterialMapper packingMaterialMapper;

    /**
     * 分页查询包装材料
     */
    public Page<PackingMaterialDTO> getPackingMaterialList(String materialCode, String materialName, 
                                                          Integer materialType, Boolean isEnabled, 
                                                          Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdTime"));

        Specification<PackingMaterial> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (materialCode != null && !materialCode.trim().isEmpty()) {
                predicates.add(cb.like(root.get("materialCode"), "%" + materialCode + "%"));
            }
            if (materialName != null && !materialName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("materialName"), "%" + materialName + "%"));
            }
            if (materialType != null) {
                predicates.add(cb.equal(root.get("materialType"), materialType));
            }
            if (isEnabled != null) {
                predicates.add(cb.equal(root.get("isEnabled"), isEnabled));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<PackingMaterial> pageResult = packingMaterialRepository.findAll(spec, pageable);
        return pageResult.map(packingMaterialMapper::toDTO);
    }

    /**
     * 根据ID获取包装材料详情
     */
    public PackingMaterialDTO getPackingMaterialById(Long id) {
        PackingMaterial material = packingMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("包装材料不存在"));
        return packingMaterialMapper.toDTO(material);
    }

    /**
     * 创建包装材料
     */
    @Transactional
    public PackingMaterialDTO createPackingMaterial(PackingMaterialDTO request) {
        // 检查编码是否已存在
        if (packingMaterialRepository.existsByMaterialCode(request.getMaterialCode())) {
            throw new RuntimeException("包装材料编码已存在");
        }

        PackingMaterial material = new PackingMaterial();
        material.setMaterialCode(request.getMaterialCode());
        material.setMaterialName(request.getMaterialName());
        material.setMaterialType(request.getMaterialType());
        material.setSpecification(request.getSpecification());
        material.setUnitPrice(request.getUnitPrice());
        material.setUnit(request.getUnit() != null ? request.getUnit() : "个");
        material.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);
        material.setRemark(request.getRemark());

        material = packingMaterialRepository.save(material);
        return packingMaterialMapper.toDTO(material);
    }

    /**
     * 更新包装材料
     */
    @Transactional
    public PackingMaterialDTO updatePackingMaterial(Long id, PackingMaterialDTO request) {
        PackingMaterial material = packingMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("包装材料不存在"));

        // 检查编码是否被其他记录使用
        if (!material.getMaterialCode().equals(request.getMaterialCode()) && 
            packingMaterialRepository.existsByMaterialCode(request.getMaterialCode())) {
            throw new RuntimeException("包装材料编码已存在");
        }

        material.setMaterialCode(request.getMaterialCode());
        material.setMaterialName(request.getMaterialName());
        material.setMaterialType(request.getMaterialType());
        material.setSpecification(request.getSpecification());
        material.setUnitPrice(request.getUnitPrice());
        material.setUnit(request.getUnit());
        material.setIsEnabled(request.getIsEnabled());
        material.setRemark(request.getRemark());

        material = packingMaterialRepository.save(material);
        return packingMaterialMapper.toDTO(material);
    }

    /**
     * 删除包装材料
     */
    @Transactional
    public void deletePackingMaterial(Long id) {
        PackingMaterial material = packingMaterialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("包装材料不存在"));

        // 检查是否被使用
        // TODO: 检查是否被打包任务使用

        packingMaterialRepository.deleteById(id);
    }

    /**
     * 获取启用的包装材料列表
     */
    public List<PackingMaterialDTO> getEnabledPackingMaterials() {
        List<PackingMaterial> materials = packingMaterialRepository.findByIsEnabledTrue();
        return packingMaterialMapper.toDTOList(materials);
    }

    /**
     * 根据类型获取启用的包装材料列表
     */
    public List<PackingMaterialDTO> getEnabledPackingMaterialsByType(Integer materialType) {
        List<PackingMaterial> materials = packingMaterialRepository.findEnabledByMaterialType(materialType);
        return packingMaterialMapper.toDTOList(materials);
    }
}
