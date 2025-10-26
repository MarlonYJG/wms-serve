package com.bj.wms.service;

import com.bj.wms.entity.InboundOrderItem;
import com.bj.wms.entity.Inventory;
import com.bj.wms.entity.InventoryTransaction;
import com.bj.wms.entity.ProductSku;
import com.bj.wms.entity.PutawayTask;
import com.bj.wms.entity.StorageLocation;
import com.bj.wms.entity.StorageZone;
import com.bj.wms.repository.InboundOrderItemRepository;
import com.bj.wms.repository.InventoryRepository;
import com.bj.wms.repository.InventoryTransactionRepository;
import com.bj.wms.repository.ProductSkuRepository;
import com.bj.wms.repository.PutawayTaskRepository;
import com.bj.wms.repository.StorageLocationRepository;
import com.bj.wms.repository.StorageZoneRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PutawayTaskService {

    private final PutawayTaskRepository putawayTaskRepository;
    private final InboundOrderItemRepository inboundOrderItemRepository;
    private final ProductSkuRepository productSkuRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final StorageZoneRepository storageZoneRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public Page<PutawayTask> page(Integer page, Integer size, Long inboundOrderId, Integer status) {
        int pageNumber = page == null || page < 1 ? 0 : page - 1;
        int pageSize = size == null || size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Specification<PutawayTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (inboundOrderId != null) {
                // 通过子查询命中过滤 inboundOrderItemId 属于该入库单的任务
                var sub = query.subquery(Long.class);
                var item = sub.from(InboundOrderItem.class);
                sub.select(item.get("id")).where(cb.equal(item.get("inboundOrderId"), inboundOrderId));
                predicates.add(root.get("inboundOrderItemId").in(sub));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return putawayTaskRepository.findAll(spec, pageable);
    }

    public List<PutawayTask> listByInboundOrderId(Long inboundOrderId) {
        // 复用分页查询的 where 仅不分页
        return page(1, Integer.MAX_VALUE, inboundOrderId, null).getContent();
    }

    /**
     * 获取带有关联信息的上架任务DTO列表
     */
    public List<com.bj.wms.dto.PutawayTaskDTO> listByInboundOrderIdWithDetails(Long inboundOrderId) {
        List<PutawayTask> tasks = listByInboundOrderId(inboundOrderId);
        
        // 获取所有相关的入库明细、商品和库位信息
        List<Long> itemIds = tasks.stream().map(PutawayTask::getInboundOrderItemId).distinct().toList();
        List<InboundOrderItem> items = inboundOrderItemRepository.findAllById(itemIds);
        
        List<Long> productSkuIds = items.stream().map(InboundOrderItem::getProductSkuId).distinct().toList();
        List<ProductSku> productSkus = productSkuRepository.findAllById(productSkuIds);
        
        List<Long> locationIds = tasks.stream()
                .flatMap(task -> {
                    List<Long> ids = new ArrayList<>();
                    if (task.getFromLocationId() != null) ids.add(task.getFromLocationId());
                    if (task.getToLocationId() != null) ids.add(task.getToLocationId());
                    return ids.stream();
                })
                .distinct()
                .toList();
        List<StorageLocation> locations = storageLocationRepository.findAllById(locationIds);
        
        // 创建映射
        var itemMap = items.stream().collect(java.util.stream.Collectors.toMap(InboundOrderItem::getId, item -> item));
        var productSkuMap = productSkus.stream().collect(java.util.stream.Collectors.toMap(ProductSku::getId, sku -> sku));
        var locationMap = locations.stream().collect(java.util.stream.Collectors.toMap(StorageLocation::getId, loc -> loc));
        
        // 转换为带关联信息的DTO
        return tasks.stream()
                .map(task -> com.bj.wms.mapper.PutawayTaskMapper.toDTO(task, itemMap, productSkuMap, locationMap))
                .toList();
    }

    public Optional<PutawayTask> findById(Long id) {
        return putawayTaskRepository.findById(id);
    }

    @Transactional
    public List<PutawayTask> generate(Long inboundOrderItemId, String putawayStrategy) {
        InboundOrderItem item = inboundOrderItemRepository.findById(inboundOrderItemId)
                .orElseThrow(() -> new IllegalArgumentException("入库明细不存在"));
        // 简化：按明细的 receivedQuantity 生成单条上架任务，策略暂不生效
        int quantity = item.getReceivedQuantity() == null ? 0 : item.getReceivedQuantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("该入库明细暂无可上架数量");
        }
        PutawayTask task = new PutawayTask();
        task.setTaskNo(generateTaskNo());
        task.setInboundOrderItemId(inboundOrderItemId);
        task.setQuantity(quantity);
        // 简化：由前端或后续接口指定上架库位，这里先设为 null，避免外键约束错误
        task.setToLocationId(null);
        task.setStatus(1);
        PutawayTask saved = putawayTaskRepository.save(task);
        return java.util.Collections.singletonList(saved);
    }

    @Transactional
    public PutawayTask start(Long taskId, Integer operatorId) {
        PutawayTask task = putawayTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("上架任务不存在"));
        if (task.getStatus() != null && task.getStatus() >= 2) {
            return task; // 已开始/完成不重复处理
        }
        task.setStatus(2);
        if (operatorId != null) {
            task.setOperator(operatorId);
        }
        return putawayTaskRepository.save(task);
    }

    @Transactional
    public PutawayTask complete(Long taskId) {
        PutawayTask task = putawayTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("上架任务不存在"));
        
        if (task.getStatus() != 2) {
            throw new IllegalArgumentException("只有进行中状态的任务才能完成");
        }
        
        // 更新任务状态
        task.setStatus(3);
        PutawayTask savedTask = putawayTaskRepository.save(task);
        
        // 同步库存数据
        updateInventoryForPutaway(task);
        
        return savedTask;
    }
    
    /**
     * 上架完成后更新库存数据
     */
    private void updateInventoryForPutaway(PutawayTask task) {
        // 获取入库明细信息
        InboundOrderItem item = inboundOrderItemRepository.findById(task.getInboundOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("入库明细不存在"));
        
        // 获取目标库位信息
        StorageLocation targetLocation = storageLocationRepository.findById(task.getToLocationId())
                .orElseThrow(() -> new IllegalArgumentException("目标库位不存在"));
        
        // 获取商品信息
        ProductSku productSku = productSkuRepository.findById(item.getProductSkuId())
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        
        // 获取库区信息以获取仓库ID
        StorageZone zone = storageZoneRepository.findById(targetLocation.getZoneId())
                .orElseThrow(() -> new IllegalArgumentException("库区不存在"));
        
        // 检查是否已存在相同商品、批次、库位的库存记录
        List<Inventory> existingInventories = inventoryRepository.findByWarehouseIdAndLocationIdAndProductSkuId(
                zone.getWarehouseId(), 
                task.getToLocationId(), 
                item.getProductSkuId()
        );
        
        Inventory inventory;
        boolean isNewInventory = false;
        
        if (existingInventories.isEmpty()) {
            // 创建新的库存记录
            inventory = new Inventory();
            inventory.setWarehouseId(zone.getWarehouseId());
            inventory.setLocationId(task.getToLocationId());
            inventory.setProductSkuId(item.getProductSkuId());
            inventory.setBatchNo(item.getBatchNo());
            inventory.setProductionDate(item.getProductionDate());
            inventory.setExpiryDate(item.getExpiryDate());
            inventory.setQuantity(task.getQuantity());
            inventory.setLockedQuantity(0);
            isNewInventory = true;
        } else {
            // 更新现有库存记录（累加数量）
            inventory = existingInventories.get(0);
            inventory.setQuantity(inventory.getQuantity() + task.getQuantity());
        }
        
        // 保存库存记录
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        // 记录库存交易流水
        recordInventoryTransaction(savedInventory, task.getQuantity(), task.getTaskNo(), isNewInventory);
        
        log.info("上架任务完成，库存已同步: 任务={}, 商品={}, 库位={}, 数量={}", 
                task.getTaskNo(), productSku.getSkuCode(), targetLocation.getLocationCode(), task.getQuantity());
    }
    
    /**
     * 记录库存交易流水
     */
    private void recordInventoryTransaction(Inventory inventory, Integer quantityChange, String relatedOrderNo, boolean isNewInventory) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductSkuId(inventory.getProductSkuId());
        transaction.setBatchNo(inventory.getBatchNo());
        transaction.setWarehouseId(inventory.getWarehouseId());
        transaction.setLocationId(inventory.getLocationId());
        transaction.setTransactionType(isNewInventory ? 5 : 6); // 5-上架新增，6-上架增加
        transaction.setRelatedOrderNo(relatedOrderNo);
        transaction.setQuantityChange(quantityChange);
        transaction.setQuantityAfter(inventory.getQuantity());
        transaction.setTransactionTime(LocalDateTime.now());
        inventoryTransactionRepository.save(transaction);
    }

    @Transactional
    public PutawayTask updateLocation(Long taskId, Long toLocationId) {
        PutawayTask task = putawayTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("上架任务不存在"));
        
        if (task.getStatus() != 1) {
            throw new IllegalArgumentException("只有待执行状态的任务才能更新库位");
        }
        
        task.setToLocationId(toLocationId);
        return putawayTaskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        PutawayTask task = putawayTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("上架任务不存在"));

        // 允许删除待执行状态和未知状态的任务
        if (task.getStatus() != null && task.getStatus() == 3) { // 3: 已完成
            throw new IllegalArgumentException("已完成的任务不能删除");
        }

        putawayTaskRepository.delete(task);
    }

    private String generateTaskNo() {
        String no = "PT" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        if (putawayTaskRepository.existsByTaskNo(no)) {
            return generateTaskNo();
        }
        return no;
    }
}


