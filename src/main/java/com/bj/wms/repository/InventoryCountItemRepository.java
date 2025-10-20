package com.bj.wms.repository;

import com.bj.wms.entity.InventoryCountItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InventoryCountItemRepository extends JpaRepository<InventoryCountItem, Long>, JpaSpecificationExecutor<InventoryCountItem> {
    List<InventoryCountItem> findByCountId(Long countId);
    void deleteByCountId(Long countId);
}


