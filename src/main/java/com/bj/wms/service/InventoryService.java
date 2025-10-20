package com.bj.wms.service;

import com.bj.wms.dto.InventoryDTO;
import com.bj.wms.entity.Inventory;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.entity.Warehouse;
import com.bj.wms.mapper.InventoryMapper;
import com.bj.wms.repository.InventoryRepository;
import com.bj.wms.repository.InventoryTransactionRepository;
import com.bj.wms.repository.ProductSkuRepository;
import com.bj.wms.repository.StorageLocationRepository;
import com.bj.wms.repository.WarehouseRepository;
import com.bj.wms.util.PageResult;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final ProductSkuRepository productSkuRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public PageResult<InventoryDTO> page(Integer page, Integer size, Long warehouseId, Long locationId, Long productSkuId, String skuCode, String batchNo, Boolean hasStock) {
        Pageable pageable = PageRequest.of(page == null || page < 1 ? 0 : page - 1, size == null || size < 1 ? 10 : size, Sort.by(Sort.Direction.DESC, "createdTime"));

        Specification<Inventory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (warehouseId != null) predicates.add(cb.equal(root.get("warehouseId"), warehouseId));
            if (locationId != null) predicates.add(cb.equal(root.get("locationId"), locationId));
            if (productSkuId != null) predicates.add(cb.equal(root.get("productSkuId"), productSkuId));
            if (batchNo != null && !batchNo.isBlank()) predicates.add(cb.equal(root.get("batchNo"), batchNo));
            if (Boolean.TRUE.equals(hasStock)) {
                predicates.add(cb.greaterThan(root.get("quantity"), 0));
            }
            // skuCode 模糊匹配需要借助后续映射，先忽略，或可通过联表，但为保持 JPA 简洁先略
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Inventory> pageData = inventoryRepository.findAll(spec, pageable);

        List<InventoryDTO> content = pageData.getContent().stream().map(inv -> {
            String warehouseName = warehouseRepository.findById(inv.getWarehouseId()).map(Warehouse::getName).orElse(null);
            String locationCode = storageLocationRepository.findById(inv.getLocationId()).map(StorageLocation::getLocationCode).orElse(null);
            ProductSku sku = productSkuRepository.findById(inv.getProductSkuId()).orElse(null);
            String code = sku == null ? null : sku.getSkuCode();
            String name = sku == null ? null : sku.getSkuName();
            return InventoryMapper.toDTO(inv, warehouseName, locationCode, code, name);
        }).toList();

        return new PageResult<>(content, pageData.getTotalElements());
    }

    public Optional<Inventory> getById(Long id) { return inventoryRepository.findById(id); }

    @Transactional
    public Inventory adjust(Long id, int delta, String reason) {
        Inventory inv = inventoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("库存不存在"));
        int newQty = (inv.getQuantity() == null ? 0 : inv.getQuantity()) + delta;
        if (newQty < 0) throw new IllegalArgumentException("库存不足，无法调整");
        inv.setQuantity(newQty);
        Inventory saved = inventoryRepository.save(inv);

        // 写入库存流水（调整）
        com.bj.wms.entity.InventoryTransaction tx = new com.bj.wms.entity.InventoryTransaction();
        tx.setProductSkuId(saved.getProductSkuId());
        tx.setBatchNo(saved.getBatchNo());
        tx.setWarehouseId(saved.getWarehouseId());
        tx.setLocationId(saved.getLocationId());
        tx.setTransactionType(4);
        tx.setRelatedOrderNo(null);
        tx.setQuantityChange(delta);
        tx.setQuantityAfter(saved.getQuantity());
        tx.setTransactionTime(java.time.LocalDateTime.now());
        inventoryTransactionRepository.save(tx);

        return saved;
    }

    @Transactional
    public Inventory transfer(Long id, Long toLocationId, int quantity, String reason) {
        Inventory inv = inventoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("库存不存在"));
        if (quantity <= 0 || quantity > (inv.getQuantity() == null ? 0 : inv.getQuantity())) {
            throw new IllegalArgumentException("转移数量不合法");
        }
        // 扣减来源库位
        inv.setQuantity(inv.getQuantity() - quantity);
        Inventory invSaved = inventoryRepository.save(inv);

        // 增加目标库位库存（同产品同批次）
        Inventory to = new Inventory();
        to.setWarehouseId(inv.getWarehouseId());
        to.setLocationId(toLocationId);
        to.setProductSkuId(inv.getProductSkuId());
        to.setBatchNo(inv.getBatchNo());
        to.setProductionDate(inv.getProductionDate());
        to.setExpiryDate(inv.getExpiryDate());
        to.setQuantity(quantity);
        to.setLockedQuantity(0);
        Inventory toSaved = inventoryRepository.save(to);

        // 写入库存流水（移库）两条：来源-、目标+
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        com.bj.wms.entity.InventoryTransaction txOut = new com.bj.wms.entity.InventoryTransaction();
        txOut.setProductSkuId(invSaved.getProductSkuId());
        txOut.setBatchNo(invSaved.getBatchNo());
        txOut.setWarehouseId(invSaved.getWarehouseId());
        txOut.setLocationId(invSaved.getLocationId());
        txOut.setTransactionType(3);
        txOut.setQuantityChange(-quantity);
        txOut.setQuantityAfter(invSaved.getQuantity());
        txOut.setTransactionTime(now);
        inventoryTransactionRepository.save(txOut);

        com.bj.wms.entity.InventoryTransaction txIn = new com.bj.wms.entity.InventoryTransaction();
        txIn.setProductSkuId(toSaved.getProductSkuId());
        txIn.setBatchNo(toSaved.getBatchNo());
        txIn.setWarehouseId(toSaved.getWarehouseId());
        txIn.setLocationId(toSaved.getLocationId());
        txIn.setTransactionType(3);
        txIn.setQuantityChange(quantity);
        txIn.setQuantityAfter(toSaved.getQuantity());
        txIn.setTransactionTime(now);
        inventoryTransactionRepository.save(txIn);

        return toSaved;
    }
}


