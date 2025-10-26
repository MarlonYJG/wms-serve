package com.bj.wms.service;

import com.bj.wms.dto.InventoryCountDTO;
import com.bj.wms.dto.InventoryCountItemDTO;
import com.bj.wms.entity.Inventory;
import com.bj.wms.entity.InventoryCount;
import com.bj.wms.entity.InventoryCountItem;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.entity.Warehouse;
import com.bj.wms.mapper.InventoryCountMapper;
import com.bj.wms.repository.*;
import com.bj.wms.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InventoryCountService {

    private final InventoryCountRepository countRepository;
    private final InventoryCountItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final ProductSkuRepository productSkuRepository;
    private final InventoryRepository inventoryRepository;

    public PageResult<InventoryCountDTO> page(Integer page, Integer size, Long warehouseId, Integer status) {
        Pageable pageable = PageRequest.of(page == null || page < 1 ? 0 : page - 1, size == null || size < 1 ? 10 : size, Sort.by(Sort.Direction.DESC, "createdTime"));
        Specification<InventoryCount> spec = (root, query, cb) -> {
            var p = cb.conjunction();
            if (warehouseId != null) p.getExpressions().add(cb.equal(root.get("warehouseId"), warehouseId));
            if (status != null) p.getExpressions().add(cb.equal(root.get("status"), status));
            return p;
        };
        Page<InventoryCount> pageData = countRepository.findAll(spec, pageable);
        List<InventoryCountDTO> content = pageData.getContent().stream().map(e -> toDetail(e.getId())).toList();
        return new PageResult<>(content, page, size, pageData.getTotalElements());
    }

    public InventoryCountDTO toDetail(Long id) {
        InventoryCount e = countRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("盘点单不存在"));
        String warehouseName = warehouseRepository.findById(e.getWarehouseId()).map(Warehouse::getName).orElse(null);
        List<InventoryCountItem> items = itemRepository.findByCountId(id);
        List<InventoryCountItemDTO> itemDTOs = items.stream().map(it -> {
            String locationCode = storageLocationRepository.findById(it.getLocationId()).map(StorageLocation::getLocationCode).orElse(null);
            ProductSku sku = productSkuRepository.findById(it.getProductSkuId()).orElse(null);
            String code = sku == null ? null : sku.getSkuCode();
            String name = sku == null ? null : sku.getSkuName();
            return InventoryCountMapper.toItemDTO(it, locationCode, code, name);
        }).toList();
        return InventoryCountMapper.toDTO(e, warehouseName, itemDTOs);
    }

    @Transactional
    public InventoryCountDTO create(Long warehouseId, String remark, List<Long> locationIds) {
        InventoryCount e = new InventoryCount();
        e.setCountNo(genNo());
        e.setWarehouseId(warehouseId);
        e.setStatus(1);
        e.setRemark(remark);
        InventoryCount saved = countRepository.save(e);

        // 预生成盘点明细（根据库位现有库存）
        if (locationIds != null && !locationIds.isEmpty()) {
            for (Long locId : locationIds) {
                List<Inventory> stocks = inventoryRepository.findAll((root, q, cb) -> cb.equal(root.get("locationId"), locId));
                for (Inventory inv : stocks) {
                    InventoryCountItem item = new InventoryCountItem();
                    item.setCountId(saved.getId());
                    item.setLocationId(inv.getLocationId());
                    item.setProductSkuId(inv.getProductSkuId());
                    item.setBatchNo(inv.getBatchNo());
                    item.setSystemQty(inv.getQuantity());
                    item.setCountedQty(null);
                    item.setDifferenceQty(null);
                    itemRepository.save(item);
                }
            }
        }
        return toDetail(saved.getId());
    }

    @Transactional
    public InventoryCountDTO start(Long id) {
        InventoryCount e = countRepository.findById(id).orElseThrow();
        e.setStatus(2);
        countRepository.save(e);
        return toDetail(id);
    }

    @Transactional
    public InventoryCountDTO submit(Long id, List<InventoryCountItemDTO> inputs) {
        InventoryCount e = countRepository.findById(id).orElseThrow();
        Map<Long, InventoryCountItem> idToItem = new HashMap<>();
        for (InventoryCountItem it : itemRepository.findByCountId(id)) idToItem.put(it.getId(), it);
        for (InventoryCountItemDTO dto : inputs) {
            InventoryCountItem it = idToItem.get(dto.getId());
            if (it == null) continue;
            it.setCountedQty(dto.getCountedQty());
            Integer sys = it.getSystemQty() == null ? 0 : it.getSystemQty();
            Integer cnt = it.getCountedQty() == null ? 0 : it.getCountedQty();
            it.setDifferenceQty(cnt - sys);
            itemRepository.save(it);
        }
        e.setStatus(3);
        countRepository.save(e);
        return toDetail(id);
    }

    @Transactional
    public InventoryCountDTO complete(Long id, boolean autoAdjust, InventoryService inventoryService) {
        InventoryCount e = countRepository.findById(id).orElseThrow();
        if (Boolean.TRUE.equals(autoAdjust)) {
            for (InventoryCountItem it : itemRepository.findByCountId(id)) {
                Integer diff = it.getDifferenceQty() == null ? 0 : it.getDifferenceQty();
                if (diff != 0) {
                    // 根据差异调整库存（正数增加，负数减少）
                    // 找到对应库存记录，简单起见按相同 locationId/productSkuId/batchNo 匹配一条（生产中应有唯一索引）
                    Optional<Inventory> invOpt = inventoryRepository.findAll((root, q, cb) -> cb.and(
                            cb.equal(root.get("locationId"), it.getLocationId()),
                            cb.equal(root.get("productSkuId"), it.getProductSkuId()),
                            it.getBatchNo() == null ? cb.isNull(root.get("batchNo")) : cb.equal(root.get("batchNo"), it.getBatchNo())
                    )).stream().findFirst();
                    if (invOpt.isPresent()) {
                        inventoryService.adjust(invOpt.get().getId(), diff, "盘点差异自动调整");
                    }
                }
            }
        }
        e.setStatus(4);
        countRepository.save(e);
        return toDetail(id);
    }

    private String genNo() {
        String no = "IC" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        if (countRepository.existsByCountNo(no)) return genNo();
        return no;
    }
}


