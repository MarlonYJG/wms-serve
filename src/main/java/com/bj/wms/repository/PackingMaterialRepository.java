package com.bj.wms.repository;

import com.bj.wms.entity.PackingMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackingMaterialRepository extends JpaRepository<PackingMaterial, Long>, JpaSpecificationExecutor<PackingMaterial> {
    
    boolean existsByMaterialCode(String materialCode);
    
    Optional<PackingMaterial> findByMaterialCode(String materialCode);
    
    List<PackingMaterial> findByMaterialType(Integer materialType);
    
    List<PackingMaterial> findByIsEnabledTrue();
    
    @Query("SELECT pm FROM PackingMaterial pm WHERE pm.materialType = :materialType AND pm.isEnabled = true")
    List<PackingMaterial> findEnabledByMaterialType(@Param("materialType") Integer materialType);
}
