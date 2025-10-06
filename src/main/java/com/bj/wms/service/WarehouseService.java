package com.bj.wms.service;

import com.bj.wms.entity.Warehouse;
import com.bj.wms.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 仓库服务类
 * 
 * 处理仓库相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    /**
     * 创建仓库
     */
    @Transactional
    public Warehouse createWarehouse(Warehouse warehouse) {
        log.info("创建仓库: {}", warehouse.getWarehouseCode());
        
        // 检查仓库编码是否已存在
        if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new RuntimeException("仓库编码已存在: " + warehouse.getWarehouseCode());
        }
        
        return warehouseRepository.save(warehouse);
    }

    /**
     * 更新仓库信息
     */
    @Transactional
    public Warehouse updateWarehouse(Long id, Warehouse warehouseDetails) {
        log.info("更新仓库: {}", id);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("仓库不存在: " + id));
        
        // 检查仓库编码是否被其他仓库使用
        if (!warehouse.getWarehouseCode().equals(warehouseDetails.getWarehouseCode()) && 
            warehouseRepository.existsByWarehouseCode(warehouseDetails.getWarehouseCode())) {
            throw new RuntimeException("仓库编码已存在: " + warehouseDetails.getWarehouseCode());
        }
        
        // 更新仓库信息
        warehouse.setWarehouseCode(warehouseDetails.getWarehouseCode());
        warehouse.setWarehouseName(warehouseDetails.getWarehouseName());
        warehouse.setDescription(warehouseDetails.getDescription());
        warehouse.setAddress(warehouseDetails.getAddress());
        warehouse.setPhone(warehouseDetails.getPhone());
        warehouse.setManager(warehouseDetails.getManager());
        warehouse.setStatus(warehouseDetails.getStatus());
        warehouse.setCapacity(warehouseDetails.getCapacity());
        
        return warehouseRepository.save(warehouse);
    }

    /**
     * 根据ID获取仓库
     */
    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    /**
     * 根据仓库编码获取仓库
     */
    public Optional<Warehouse> getWarehouseByCode(String warehouseCode) {
        return warehouseRepository.findByWarehouseCode(warehouseCode);
    }

    /**
     * 获取所有仓库（分页）
     */
    public Page<Warehouse> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findAllActive(pageable);
    }

    /**
     * 根据关键词搜索仓库
     */
    public Page<Warehouse> searchWarehouses(String keyword, Pageable pageable) {
        return warehouseRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 根据状态获取仓库
     */
    public Page<Warehouse> getWarehousesByStatus(Integer status, Pageable pageable) {
        return warehouseRepository.findByStatus(status, pageable);
    }

    /**
     * 根据负责人获取仓库
     */
    public Page<Warehouse> getWarehousesByManager(String manager, Pageable pageable) {
        return warehouseRepository.findByManager(manager, pageable);
    }

    /**
     * 获取所有启用的仓库
     */
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findActiveWarehouses();
    }

    /**
     * 获取容量使用率高的仓库
     */
    public List<Warehouse> getWarehousesWithHighUsage(Double threshold) {
        return warehouseRepository.findWarehousesWithHighUsage(threshold);
    }

    /**
     * 删除仓库（逻辑删除）
     */
    @Transactional
    public void deleteWarehouse(Long id) {
        log.info("删除仓库: {}", id);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("仓库不存在: " + id));
        
        warehouse.setDeleted(1);
        warehouseRepository.save(warehouse);
    }

    /**
     * 更新仓库使用容量
     */
    @Transactional
    public Warehouse updateUsedCapacity(Long warehouseId, Double usedCapacity) {
        log.info("更新仓库使用容量: {} -> {}", warehouseId, usedCapacity);
        
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("仓库不存在: " + warehouseId));
        
        if (usedCapacity < 0) {
            throw new RuntimeException("使用容量不能为负数");
        }
        
        if (warehouse.getCapacity() != null && usedCapacity > warehouse.getCapacity()) {
            throw new RuntimeException("使用容量不能超过仓库总容量");
        }
        
        warehouse.setUsedCapacity(usedCapacity);
        return warehouseRepository.save(warehouse);
    }

    /**
     * 计算仓库使用率
     */
    public Double calculateUsageRate(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("仓库不存在: " + warehouseId));
        
        if (warehouse.getCapacity() == null || warehouse.getCapacity() == 0) {
            return 0.0;
        }
        
        return (warehouse.getUsedCapacity() / warehouse.getCapacity()) * 100;
    }
}


