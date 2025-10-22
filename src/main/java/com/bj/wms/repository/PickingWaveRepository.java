package com.bj.wms.repository;

import com.bj.wms.entity.PickingWave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PickingWaveRepository extends JpaRepository<PickingWave, Long>, JpaSpecificationExecutor<PickingWave> {
    
    boolean existsByWaveNo(String waveNo);
    
    Optional<PickingWave> findByWaveNo(String waveNo);
    
    @EntityGraph(attributePaths = {"warehouse"})
    @Query("SELECT pw FROM PickingWave pw WHERE pw.id = :id")
    Optional<PickingWave> findByIdWithWarehouse(@Param("id") Long id);
    
    @Override
    @EntityGraph(attributePaths = {"warehouse"})
    Page<PickingWave> findAll(org.springframework.data.jpa.domain.Specification<PickingWave> spec, Pageable pageable);
}
