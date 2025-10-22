package com.bj.wms.repository;

import com.bj.wms.entity.ReviewTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewTaskRepository extends JpaRepository<ReviewTask, Long>, JpaSpecificationExecutor<ReviewTask> {
    
    boolean existsByTaskNo(String taskNo);
    
    Optional<ReviewTask> findByTaskNo(String taskNo);
    
    List<ReviewTask> findByOutboundOrderId(Long outboundOrderId);
    
    @EntityGraph(attributePaths = {"outboundOrder", "productSku"})
    @Query("SELECT rt FROM ReviewTask rt WHERE rt.id = :id")
    Optional<ReviewTask> findByIdWithDetails(@Param("id") Long id);
    
    @Override
    @EntityGraph(attributePaths = {"outboundOrder", "productSku"})
    Page<ReviewTask> findAll(org.springframework.data.jpa.domain.Specification<ReviewTask> spec, Pageable pageable);
}
