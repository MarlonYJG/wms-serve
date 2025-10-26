package com.bj.wms.service;

import com.bj.wms.dto.InventoryDTO;
import com.bj.wms.entity.Inventory;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.entity.StorageZone;
import com.bj.wms.entity.Warehouse;
import com.bj.wms.entity.ZoneType;
import com.bj.wms.mapper.InventoryMapper;
import com.bj.wms.repository.InventoryRepository;
import com.bj.wms.repository.InventoryTransactionRepository;
import com.bj.wms.repository.ProductSkuRepository;
import com.bj.wms.repository.StorageLocationRepository;
import com.bj.wms.repository.StorageZoneRepository;
import com.bj.wms.repository.WarehouseRepository;
import com.bj.wms.repository.InboundChargeRepository;
import com.bj.wms.repository.InboundOrderItemRepository;
import com.bj.wms.util.PageResult;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final StorageZoneRepository storageZoneRepository;
    private final ProductSkuRepository productSkuRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InboundChargeRepository inboundChargeRepository;
    private final InboundOrderItemRepository inboundOrderItemRepository;

    public PageResult<InventoryDTO> page(Integer page, Integer size, Long warehouseId, Long locationId, Long productSkuId, String skuCode, String batchNo, Boolean hasStock, String zoneType) {
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
            
            // 按库区类型筛选
            if (zoneType != null && !zoneType.isBlank()) {
                try {
                    // 将字符串转换为ZoneType枚举
                    ZoneType zoneTypeEnum = ZoneType.valueOf(zoneType);
                    // 通过子查询关联库位和库区
                    var subQuery = query.subquery(Long.class);
                    var locationRoot = subQuery.from(StorageLocation.class);
                    var zoneRoot = subQuery.from(StorageZone.class);
                    subQuery.select(locationRoot.get("id"))
                        .where(cb.and(
                            cb.equal(locationRoot.get("id"), root.get("locationId")),
                            cb.equal(locationRoot.get("zoneId"), zoneRoot.get("id")),
                            cb.equal(zoneRoot.get("zoneType"), zoneTypeEnum)
                        ));
                    predicates.add(cb.exists(subQuery));
                } catch (IllegalArgumentException e) {
                    // 如果zoneType无效，忽略筛选条件
                    log.warn("无效的库区类型: {}", zoneType);
                }
            }
            
            // skuCode 模糊匹配需要借助后续映射，先忽略，或可通过联表，但为保持 JPA 简洁先略
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Inventory> pageData = inventoryRepository.findAll(spec, pageable);

        // 批量获取关联数据，避免N+1查询问题
        List<Long> warehouseIds = pageData.getContent().stream().map(Inventory::getWarehouseId).distinct().toList();
        List<Long> locationIds = pageData.getContent().stream().map(Inventory::getLocationId).distinct().toList();
        List<Long> productSkuIds = pageData.getContent().stream().map(Inventory::getProductSkuId).distinct().toList();
        
        Map<Long, String> warehouseNameMap = warehouseRepository.findAllById(warehouseIds)
            .stream().collect(java.util.stream.Collectors.toMap(Warehouse::getId, Warehouse::getName));
        Map<Long, StorageLocation> locationMap = storageLocationRepository.findAllById(locationIds)
            .stream().collect(java.util.stream.Collectors.toMap(StorageLocation::getId, loc -> loc));
        Map<Long, ProductSku> productSkuMap = productSkuRepository.findAllById(productSkuIds)
            .stream().collect(java.util.stream.Collectors.toMap(ProductSku::getId, sku -> sku));

        // 获取库区信息
        List<Long> zoneIds = locationMap.values().stream().map(StorageLocation::getZoneId).distinct().toList();
        Map<Long, StorageZone> zoneMap = storageZoneRepository.findAllById(zoneIds)
            .stream().collect(java.util.stream.Collectors.toMap(StorageZone::getId, zone -> zone));

        List<InventoryDTO> content = pageData.getContent().stream().map(inv -> {
            String warehouseName = warehouseNameMap.get(inv.getWarehouseId());
            StorageLocation location = locationMap.get(inv.getLocationId());
            String locationCode = location != null ? location.getLocationCode() : null;
            ProductSku sku = productSkuMap.get(inv.getProductSkuId());
            String code = sku == null ? null : sku.getSkuCode();
            String name = sku == null ? null : sku.getSkuName();
            
            // 获取库区信息
            StorageZone zone = location != null ? zoneMap.get(location.getZoneId()) : null;
            String zoneCode = zone != null ? zone.getZoneCode() : null;
            String zoneName = zone != null ? zone.getZoneName() : null;
            String zoneTypeValue = zone != null && zone.getZoneType() != null ? zone.getZoneType().name() : null;
            String zoneTypeName = zone != null && zone.getZoneType() != null ? zone.getZoneType().getDescription() : null;
            
            // 获取价格信息
            java.math.BigDecimal purchasePrice = sku != null ? sku.getPurchasePrice() : null;
            java.math.BigDecimal costPrice = sku != null ? sku.getCostPrice() : null;
            java.math.BigDecimal salePrice = sku != null ? sku.getSalePrice() : null;
            java.math.BigDecimal retailPrice = sku != null ? sku.getRetailPrice() : null;
            
            // 计算入库费用分摊
            java.math.BigDecimal inboundCharges = java.math.BigDecimal.ZERO;
            java.math.BigDecimal unitInboundCharges = java.math.BigDecimal.ZERO;
            
            if (sku != null && inv.getQuantity() != null && inv.getQuantity() > 0) {
                // 查找相关的入库单明细
                List<com.bj.wms.entity.InboundOrderItem> orderItems = inboundOrderItemRepository
                    .findByProductSkuIdAndReceivedQuantityGreaterThan(sku.getId(), 0);
                
                if (!orderItems.isEmpty()) {
                    // 计算总入库费用
                    java.math.BigDecimal totalCharges = java.math.BigDecimal.ZERO;
                    int totalReceivedQuantity = 0;
                    
                    for (com.bj.wms.entity.InboundOrderItem item : orderItems) {
                        // 获取该入库单的费用
                        List<com.bj.wms.entity.InboundCharge> charges = inboundChargeRepository
                            .findByInboundOrderIdAndDeletedFalse(item.getInboundOrderId());
                        
                        java.math.BigDecimal orderCharges = charges.stream()
                            .map(com.bj.wms.entity.InboundCharge::getAmount)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                        
                        // 按收货数量分摊费用
                        if (item.getReceivedQuantity() != null && item.getReceivedQuantity() > 0) {
                            totalCharges = totalCharges.add(orderCharges);
                            totalReceivedQuantity += item.getReceivedQuantity();
                        }
                    }
                    
                    if (totalReceivedQuantity > 0) {
                        // 计算单位费用分摊
                        unitInboundCharges = totalCharges.divide(java.math.BigDecimal.valueOf(totalReceivedQuantity), 4, java.math.RoundingMode.HALF_UP);
                        // 计算当前库存的费用分摊
                        inboundCharges = unitInboundCharges.multiply(java.math.BigDecimal.valueOf(inv.getQuantity()));
                    }
                }
            }
            
            return InventoryMapper.toDTO(inv, warehouseName, locationCode, code, name, 
                                       zoneCode, zoneName, zoneTypeValue, zoneTypeName,
                                       purchasePrice, costPrice, salePrice, retailPrice,
                                       inboundCharges, unitInboundCharges);
        }).toList();

        return new PageResult<>(content, page, size, pageData.getTotalElements());
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

    /**
     * 根据商品SKU ID和仓库ID查询库存
     */
    public List<InventoryDTO> getInventoryByProductSku(Long productSkuId, Long warehouseId) {
        List<Inventory> inventories;
        
        if (warehouseId != null) {
            inventories = inventoryRepository.findByWarehouseIdAndProductSkuIdAndQuantityGreaterThan(
                warehouseId, productSkuId, 0);
        } else {
            inventories = inventoryRepository.findByProductSkuIdAndQuantityGreaterThan(productSkuId, 0);
        }
        
        return inventories.stream()
            .map(inv -> {
                // 获取关联信息
                var warehouse = warehouseRepository.findById(inv.getWarehouseId()).orElse(null);
                var location = storageLocationRepository.findById(inv.getLocationId()).orElse(null);
                var sku = productSkuRepository.findById(inv.getProductSkuId()).orElse(null);
                
                String warehouseName = warehouse != null ? warehouse.getName() : "";
                String locationCode = location != null ? location.getLocationCode() : "";
                String skuCode = sku != null ? sku.getSkuCode() : "";
                String productName = sku != null ? sku.getSkuName() : "";
                
                return InventoryMapper.toDTO(inv, warehouseName, locationCode, skuCode, productName);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}


