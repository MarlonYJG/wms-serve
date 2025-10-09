package com.bj.wms.service;

import com.bj.wms.dto.StorageZoneQueryDTO;
import com.bj.wms.entity.StorageZone;
import com.bj.wms.entity.ZoneType;
import com.bj.wms.repository.StorageZoneRepository;
import com.bj.wms.util.PageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageZoneService {

    private final StorageZoneRepository storageZoneRepository;

    public Page<StorageZone> page(StorageZoneQueryDTO query) {
        Pageable pageable = PageUtil.createPageable(
                PageUtil.validatePage(query.getPage()),
                PageUtil.validateSize(query.getSize()),
                PageUtil.validateSortBy(query.getSortBy()),
                PageUtil.validateSortDirection(query.getSortDir())
        );

        Specification<StorageZone> spec = (root, cq, cb) -> {
            var predicate = cb.conjunction();
            // 仅查询未删除
            predicate.getExpressions().add(cb.equal(root.get("deleted"), 0));
            if (query.getWarehouseId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("warehouseId"), query.getWarehouseId()));
            }
            if (query.getZoneType() != null && !query.getZoneType().isBlank()) {
                ZoneType type = ZoneType.from(query.getZoneType());
                predicate.getExpressions().add(cb.equal(root.get("zoneType"), type));
            }
            if (query.getIsEnabled() != null) {
                predicate.getExpressions().add(cb.equal(root.get("isEnabled"), query.getIsEnabled()));
            }
            if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
                String like = "%" + query.getKeyword().trim() + "%";
                predicate.getExpressions().add(cb.or(
                        cb.like(root.get("zoneName"), like),
                        cb.like(root.get("zoneCode"), like)
                ));
            }
            return predicate;
        };
        return storageZoneRepository.findAll(spec, pageable);
    }

    public Optional<StorageZone> getById(Long id) {
        return storageZoneRepository.findById(id);
    }

    @Transactional
    public StorageZone create(StorageZone zone) {
        if (storageZoneRepository.existsByZoneCode(zone.getZoneCode())) {
            throw new IllegalArgumentException("库区编码已存在");
        }
        return storageZoneRepository.save(zone);
    }

    @Transactional
    public StorageZone update(StorageZone zone) {
        StorageZone exist = storageZoneRepository.findById(zone.getId())
                .orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        if (!exist.getZoneCode().equals(zone.getZoneCode()) && storageZoneRepository.existsByZoneCode(zone.getZoneCode())) {
            throw new IllegalArgumentException("库区编码已存在");
        }
        exist.setWarehouseId(zone.getWarehouseId());
        exist.setZoneCode(zone.getZoneCode());
        exist.setZoneName(zone.getZoneName());
        exist.setZoneType(zone.getZoneType());
        exist.setCapacity(zone.getCapacity());
        exist.setUsedCapacity(zone.getUsedCapacity());
        exist.setIsEnabled(zone.getIsEnabled() == null ? exist.getIsEnabled() : zone.getIsEnabled());
        return storageZoneRepository.save(exist);
    }

    @Transactional
    public void deleteById(Long id) {
        StorageZone exist = storageZoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        exist.setDeleted(1);
        storageZoneRepository.save(exist);
    }

    @Transactional
    public StorageZone updateStatus(Long id, Boolean isEnabled) {
        StorageZone exist = storageZoneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        exist.setIsEnabled(Boolean.TRUE.equals(isEnabled));
        return storageZoneRepository.save(exist);
    }
}


