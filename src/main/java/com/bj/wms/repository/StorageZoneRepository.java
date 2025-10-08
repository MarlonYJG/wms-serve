/*
 * @Author: Marlon
 * @Date: 2025-10-08 20:06:39
 * @Description: 
 */
package com.bj.wms.repository;

import com.bj.wms.entity.StorageZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StorageZoneRepository extends JpaRepository<StorageZone, Long>, JpaSpecificationExecutor<StorageZone> {

    boolean existsByZoneCode(String zoneCode);
}


