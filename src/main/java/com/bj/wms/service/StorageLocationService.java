package com.bj.wms.service;

import com.bj.wms.dto.StorageLocationQueryDTO;
import com.bj.wms.entity.LocationStatus;
import com.bj.wms.entity.LocationType;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.repository.StorageLocationRepository;
import com.bj.wms.util.PageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;

    public Page<StorageLocation> page(StorageLocationQueryDTO query) {
        Pageable pageable = PageUtil.createPageable(
                PageUtil.validatePage(query.getPage() == null ? 0 : query.getPage()),
                PageUtil.validateSize(query.getSize() == null ? 10 : query.getSize()),
                PageUtil.validateSortBy(query.getSortBy()),
                PageUtil.validateSortDirection(query.getSortDir())
        );

        Specification<StorageLocation> spec = (root, cq, cb) -> {
            var predicate = cb.conjunction();
            predicate.getExpressions().add(cb.equal(root.get("deleted"), 0));
            if (query.getZoneId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("zoneId"), query.getZoneId()));
            }
            if (query.getLocationType() != null) {
                predicate.getExpressions().add(cb.equal(root.get("locationType"), query.getLocationType()));
            }
            if (query.getStatus() != null) {
                predicate.getExpressions().add(cb.equal(root.get("status"), query.getStatus()));
            }
            if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
                String like = "%" + query.getKeyword().trim() + "%";
                predicate.getExpressions().add(cb.or(
                        cb.like(root.get("locationCode"), like),
                        cb.like(root.get("locationName"), like)
                ));
            }
            return predicate;
        };
        return storageLocationRepository.findAll(spec, pageable);
    }

    public Optional<StorageLocation> getById(Long id) {
        return storageLocationRepository.findById(id).filter(it -> it.getDeleted() != 1);
    }

    @Transactional
    public StorageLocation create(StorageLocation entity) {
        if (storageLocationRepository.existsByLocationCode(entity.getLocationCode())) {
            throw new IllegalArgumentException("库位编码已存在");
        }
        return storageLocationRepository.save(entity);
    }

    @Transactional
    public StorageLocation update(Long id, StorageLocation input) {
        StorageLocation exist = storageLocationRepository.findById(id)
                .filter(it -> it.getDeleted() != 1)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));
        if (!exist.getLocationCode().equals(input.getLocationCode())
                && storageLocationRepository.existsByLocationCode(input.getLocationCode())) {
            throw new IllegalArgumentException("库位编码已存在");
        }
        exist.setZoneId(input.getZoneId());
        exist.setLocationCode(input.getLocationCode());
        exist.setLocationName(input.getLocationName());
        exist.setLocationType(input.getLocationType());
        exist.setCapacity(input.getCapacity());
        exist.setCurrentVolume(input.getCurrentVolume());
        exist.setStatus(input.getStatus());
        return storageLocationRepository.save(exist);
    }

    @Transactional
    public void delete(Long id) {
        StorageLocation exist = storageLocationRepository.findById(id)
                .filter(it -> it.getDeleted() != 1)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));
        exist.setDeleted(1);
        storageLocationRepository.save(exist);
    }

    @Transactional
    public StorageLocation updateStatus(Long id, LocationStatus status) {
        StorageLocation exist = storageLocationRepository.findById(id)
                .filter(it -> it.getDeleted() != 1)
                .orElseThrow(() -> new IllegalArgumentException("库位不存在"));
        exist.setStatus(status == null ? LocationStatus.AVAILABLE : status);
        return storageLocationRepository.save(exist);
    }

    /**
     * 根据仓库ID获取可用库位
     */
    public List<StorageLocation> getAvailableLocationsByWarehouse(Long warehouseId) {
        return storageLocationRepository.findByWarehouseId(warehouseId).stream()
                .filter(location -> location.getDeleted() != 1)
                .filter(location -> location.getStatus() == LocationStatus.AVAILABLE)
                .filter(location -> location.getCurrentVolume() == null || location.getCurrentVolume().compareTo(location.getCapacity()) < 0)
                .toList();
    }
}


