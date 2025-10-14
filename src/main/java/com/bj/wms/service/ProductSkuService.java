package com.bj.wms.service;

import com.bj.wms.entity.ProductSku;
import com.bj.wms.repository.ProductSkuRepository;
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
public class ProductSkuService {

    private final ProductSkuRepository productSkuRepository;

    public Page<ProductSku> page(Integer page, Integer size,
                                 String skuCode, String skuName,
                                 Long supplierId, Boolean isBatchManaged,
                                 Boolean isExpiryManaged, String brand, Long categoryId) {
        int pageIndex = page == null || page < 1 ? 0 : page - 1;
        int pageSize = size == null || size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        Specification<ProductSku> spec = (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (skuCode != null && !skuCode.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("skuCode"), "%" + skuCode.trim() + "%"));
            }
            if (skuName != null && !skuName.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("skuName"), "%" + skuName.trim() + "%"));
            }
            if (brand != null && !brand.isBlank()) {
                predicate.getExpressions().add(cb.like(root.get("brand"), "%" + brand.trim() + "%"));
            }
            if (categoryId != null) {
                predicate.getExpressions().add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (supplierId != null) {
                predicate.getExpressions().add(cb.equal(root.get("supplierId"), supplierId));
            }
            if (isBatchManaged != null) {
                predicate.getExpressions().add(cb.equal(root.get("isBatchManaged"), isBatchManaged));
            }
            if (isExpiryManaged != null) {
                predicate.getExpressions().add(cb.equal(root.get("isExpiryManaged"), isExpiryManaged));
            }
            return predicate;
        };

        return productSkuRepository.findAll(spec, pageable);
    }

    public Optional<ProductSku> getById(Long id) {
        return productSkuRepository.findById(id);
    }

    @Transactional
    public ProductSku create(ProductSku toCreate) {
        if (productSkuRepository.existsBySkuCode(toCreate.getSkuCode())) {
            throw new IllegalArgumentException("SKU编码已存在");
        }
        return productSkuRepository.save(toCreate);
    }

    @Transactional
    public ProductSku update(Long id, ProductSku updates) {
        ProductSku existing = productSkuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品SKU不存在"));

        if (updates.getSkuCode() != null && !updates.getSkuCode().equals(existing.getSkuCode())) {
            if (productSkuRepository.existsBySkuCode(updates.getSkuCode())) {
                throw new IllegalArgumentException("SKU编码已存在");
            }
            existing.setSkuCode(updates.getSkuCode());
        }

        if (updates.getSkuName() != null) existing.setSkuName(updates.getSkuName());
        if (updates.getSpecification() != null) existing.setSpecification(updates.getSpecification());
        if (updates.getBrand() != null) existing.setBrand(updates.getBrand());
        if (updates.getCategoryId() != null) existing.setCategoryId(updates.getCategoryId());
        if (updates.getSupplierId() != null) existing.setSupplierId(updates.getSupplierId());
        if (updates.getBarcode() != null) existing.setBarcode(updates.getBarcode());
        if (updates.getWeight() != null) existing.setWeight(updates.getWeight());
        if (updates.getVolume() != null) existing.setVolume(updates.getVolume());
        if (updates.getIsBatchManaged() != null) existing.setIsBatchManaged(updates.getIsBatchManaged());
        if (updates.getIsExpiryManaged() != null) existing.setIsExpiryManaged(updates.getIsExpiryManaged());
        if (updates.getShelfLifeDays() != null) existing.setShelfLifeDays(updates.getShelfLifeDays());
        if (updates.getSafetyStock() != null) existing.setSafetyStock(updates.getSafetyStock());
        if (updates.getIsEnabled() != null) existing.setIsEnabled(updates.getIsEnabled());

        return productSkuRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        productSkuRepository.deleteById(id);
    }
}


