package com.bj.wms.repository;

import com.bj.wms.entity.PackingTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackingTaskRepository extends JpaRepository<PackingTask, Long>, JpaSpecificationExecutor<PackingTask> {
    
    boolean existsByTaskNo(String taskNo);
    
    Optional<PackingTask> findByTaskNo(String taskNo);
    
    List<PackingTask> findByOutboundOrderId(Long outboundOrderId);
    
    @Query("SELECT pt FROM PackingTask pt WHERE pt.outboundOrderId = :outboundOrderId AND pt.status = :status")
    List<PackingTask> findByOutboundOrderIdAndStatus(@Param("outboundOrderId") Long outboundOrderId, @Param("status") Integer status);
    
    @EntityGraph(attributePaths = {"outboundOrder", "packingMaterial"})
    @Query("SELECT pt FROM PackingTask pt WHERE pt.id = :id")
    Optional<PackingTask> findByIdWithDetails(@Param("id") Long id);
    
    @Override
    @EntityGraph(attributePaths = {"outboundOrder", "packingMaterial"})
    Page<PackingTask> findAll(org.springframework.data.jpa.domain.Specification<PackingTask> spec, Pageable pageable);
}
