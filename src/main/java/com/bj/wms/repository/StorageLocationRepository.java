package com.bj.wms.repository;

import com.bj.wms.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long>, JpaSpecificationExecutor<StorageLocation> {
    boolean existsByLocationCode(String locationCode);
    
    @Query("SELECT sl FROM StorageLocation sl JOIN StorageZone sz ON sl.zoneId = sz.id WHERE sz.warehouseId = :warehouseId")
    List<StorageLocation> findByWarehouseId(@Param("warehouseId") Long warehouseId);
}


