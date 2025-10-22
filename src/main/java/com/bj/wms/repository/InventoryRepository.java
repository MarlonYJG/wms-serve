package com.bj.wms.repository;

import com.bj.wms.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
    
    List<Inventory> findByWarehouseIdAndProductSkuIdAndQuantityGreaterThan(Long warehouseId, Long productSkuId, int quantity);
    
    List<Inventory> findByWarehouseIdAndProductSkuIdAndLockedQuantityGreaterThan(Long warehouseId, Long productSkuId, int lockedQuantity);
    
    List<Inventory> findByWarehouseIdAndLocationIdAndProductSkuId(Long warehouseId, Long locationId, Long productSkuId);
}


