package com.bj.wms.service;

import com.bj.wms.dto.InventoryTransactionDTO;
import com.bj.wms.entity.InventoryTransaction;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.entity.Warehouse;
import com.bj.wms.mapper.InventoryTransactionMapper;
import com.bj.wms.repository.InventoryTransactionRepository;
import com.bj.wms.repository.ProductSkuRepository;
import com.bj.wms.repository.StorageLocationRepository;
import com.bj.wms.repository.WarehouseRepository;
import com.bj.wms.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    private final InventoryTransactionRepository txRepository;
    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final ProductSkuRepository productSkuRepository;

    public PageResult<InventoryTransactionDTO> transactions(Integer page, Integer size, Long productSkuId, Long warehouseId, Long locationId, Integer transactionType, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page == null || page < 1 ? 0 : page - 1, size == null || size < 1 ? 10 : size, Sort.by(Sort.Direction.DESC, "transactionTime"));

        Specification<InventoryTransaction> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (productSkuId != null) predicates.add(cb.equal(root.get("productSkuId"), productSkuId));
            if (warehouseId != null) predicates.add(cb.equal(root.get("warehouseId"), warehouseId));
            if (locationId != null) predicates.add(cb.equal(root.get("locationId"), locationId));
            if (transactionType != null) predicates.add(cb.equal(root.get("transactionType"), transactionType));
            if (startTime != null && !startTime.isBlank()) {
                try { predicates.add(cb.greaterThanOrEqualTo(root.get("transactionTime"), LocalDateTime.parse(startTime))); } catch (Exception ignored) {}
            }
            if (endTime != null && !endTime.isBlank()) {
                try { predicates.add(cb.lessThanOrEqualTo(root.get("transactionTime"), LocalDateTime.parse(endTime))); } catch (Exception ignored) {}
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<InventoryTransaction> pageData = txRepository.findAll(spec, pageable);
        List<InventoryTransactionDTO> content = pageData.getContent().stream().map(e -> {
            String warehouseName = warehouseRepository.findById(e.getWarehouseId()).map(Warehouse::getName).orElse(null);
            String locationCode = storageLocationRepository.findById(e.getLocationId()).map(StorageLocation::getLocationCode).orElse(null);
            ProductSku sku = productSkuRepository.findById(e.getProductSkuId()).orElse(null);
            String code = sku == null ? null : sku.getSkuCode();
            String name = sku == null ? null : sku.getSkuName();
            return InventoryTransactionMapper.toDTO(e, warehouseName, locationCode, code, name);
        }).toList();

        return new PageResult<>(content, pageData.getTotalElements());
    }
}


