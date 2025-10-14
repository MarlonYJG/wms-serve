package com.bj.wms.service;

import com.bj.wms.entity.Supplier;
import com.bj.wms.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Page<Supplier> page(Integer page, Integer size, String keyword, String supplierName, String supplierCode, Boolean isEnabled) {
        int pageIndex = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Supplier> spec = (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim() + "%";
                predicate.getExpressions().add(
                        cb.or(
                                cb.like(root.get("supplierName"), like),
                                cb.like(root.get("supplierCode"), like),
                                cb.like(root.get("contactPerson"), like)
                        )
                );
            }
            if (supplierName != null && !supplierName.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("supplierName"), "%" + supplierName.trim() + "%"));
            }
            if (supplierCode != null && !supplierCode.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("supplierCode"), "%" + supplierCode.trim() + "%"));
            }
            if (isEnabled != null) {
                predicate.getExpressions().add(cb.equal(root.get("isEnabled"), isEnabled));
            }
            return predicate;
        };

        return supplierRepository.findAll(spec, pageable);
    }

    public Optional<Supplier> findById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public Supplier create(Supplier toCreate) {
        if (supplierRepository.existsBySupplierCode(toCreate.getSupplierCode())) {
            throw new IllegalArgumentException("供应商编码已存在");
        }
        return supplierRepository.save(toCreate);
    }

    @Transactional
    public Supplier update(Long id, Supplier updates) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));

        if (updates.getSupplierCode() != null && !updates.getSupplierCode().equals(existing.getSupplierCode())) {
            if (supplierRepository.existsBySupplierCode(updates.getSupplierCode())) {
                throw new IllegalArgumentException("供应商编码已存在");
            }
            existing.setSupplierCode(updates.getSupplierCode());
        }

        if (updates.getSupplierName() != null) existing.setSupplierName(updates.getSupplierName());
        if (updates.getContactPerson() != null) existing.setContactPerson(updates.getContactPerson());
        if (updates.getContactPhone() != null) existing.setContactPhone(updates.getContactPhone());
        if (updates.getEmail() != null) existing.setEmail(updates.getEmail());
        if (updates.getAddress() != null) existing.setAddress(updates.getAddress());
        if (updates.getRating() != null) existing.setRating(updates.getRating());
        if (updates.getIsEnabled() != null) existing.setIsEnabled(updates.getIsEnabled());

        return supplierRepository.save(existing);
    }

    @Transactional
    public Supplier updateStatus(Long id, Boolean isEnabled) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在"));
        existing.setIsEnabled(isEnabled);
        return supplierRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        supplierRepository.deleteById(id);
    }
}


