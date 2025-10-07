package com.bj.wms.service;

import com.bj.wms.entity.Warehouse;
import com.bj.wms.repository.WarehouseRepository;
import com.bj.wms.util.PageUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public Page<Warehouse> page(Integer page, Integer size, String sortBy, String sortDir,
                                String keyword, String name, String code, Boolean isEnabled) {
        Pageable pageable = PageUtil.createPageable(
                PageUtil.validatePage(page == null ? PageUtil.DEFAULT_PAGE : page),
                PageUtil.validateSize(size == null ? PageUtil.DEFAULT_SIZE : size),
                PageUtil.validateSortBy(sortBy),
                PageUtil.validateSortDirection(sortDir)
        );

        Specification<Warehouse> spec = (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (keyword != null && !keyword.isBlank()) {
                var like = "%" + keyword.trim() + "%";
                predicate.getExpressions().add(cb.or(
                        cb.like(root.get("name"), like),
                        cb.like(root.get("code"), like)
                ));
            }
            if (name != null && !name.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("name"), "%" + name.trim() + "%"));
            }
            if (code != null && !code.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("code"), "%" + code.trim() + "%"));
            }
            if (isEnabled != null) {
                predicate.getExpressions().add(cb.equal(root.get("isEnabled"), isEnabled));
            }
            predicate.getExpressions().add(cb.equal(root.get("deleted"), 0));
            return predicate;
        };

        return warehouseRepository.findAll(spec, pageable);
    }

    public Optional<Warehouse> getById(@NotNull Long id) {
        return warehouseRepository.findById(id).filter(w -> w.getDeleted() != 1);
    }

    @Transactional
    public Warehouse create(@Valid Warehouse warehouse) {
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new IllegalArgumentException("仓库编码已存在");
        }
        warehouse.setId(null);
        return warehouseRepository.save(warehouse);
    }

    @Transactional
    public Warehouse update(@NotNull Long id, @Valid Warehouse input) {
        Warehouse exist = warehouseRepository.findById(id)
                .filter(w -> w.getDeleted() != 1)
                .orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        if (!exist.getCode().equals(input.getCode()) && warehouseRepository.existsByCode(input.getCode())) {
            throw new IllegalArgumentException("仓库编码已存在");
        }
        exist.setName(input.getName());
        exist.setCode(input.getCode());
        exist.setAddress(input.getAddress());
        exist.setContactPerson(input.getContactPerson());
        exist.setContactPhone(input.getContactPhone());
        exist.setIsEnabled(input.getIsEnabled() == null ? exist.getIsEnabled() : input.getIsEnabled());
        exist.setTotalCapacity(input.getTotalCapacity());
        exist.setUsedCapacity(input.getUsedCapacity());
        return warehouseRepository.save(exist);
    }

    @Transactional
    public void delete(@NotNull Long id) {
        Warehouse exist = warehouseRepository.findById(id)
                .filter(w -> w.getDeleted() != 1)
                .orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        exist.setDeleted(1);
        warehouseRepository.save(exist);
    }

    @Transactional
    public Warehouse updateStatus(@NotNull Long id, @NotNull Boolean isEnabled) {
        Warehouse exist = warehouseRepository.findById(id)
                .filter(w -> w.getDeleted() != 1)
                .orElseThrow(() -> new IllegalArgumentException("仓库不存在"));
        exist.setIsEnabled(isEnabled);
        return warehouseRepository.save(exist);
    }
}


