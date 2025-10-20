package com.bj.wms.repository;

import com.bj.wms.entity.InventoryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryCountRepository extends JpaRepository<InventoryCount, Long>, JpaSpecificationExecutor<InventoryCount> {
    boolean existsByCountNo(String countNo);
}


