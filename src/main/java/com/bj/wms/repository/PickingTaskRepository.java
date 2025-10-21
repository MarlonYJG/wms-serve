package com.bj.wms.repository;

import com.bj.wms.entity.PickingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PickingTaskRepository extends JpaRepository<PickingTask, Long>, JpaSpecificationExecutor<PickingTask> {
    
    boolean existsByTaskNo(String taskNo);
    
    Optional<PickingTask> findByTaskNo(String taskNo);
    
    List<PickingTask> findByOutboundOrderId(Long outboundOrderId);
    
    List<PickingTask> findByWaveNo(String waveNo);
    
    @Query("SELECT pt FROM PickingTask pt WHERE pt.outboundOrderId = :outboundOrderId AND pt.status = :status")
    List<PickingTask> findByOutboundOrderIdAndStatus(@Param("outboundOrderId") Long outboundOrderId, @Param("status") Integer status);
    
    @Query("SELECT pt FROM PickingTask pt WHERE pt.outboundOrderId = :outboundOrderId AND pt.productSkuId = :productSkuId")
    List<PickingTask> findByOutboundOrderIdAndProductSkuId(@Param("outboundOrderId") Long outboundOrderId, @Param("productSkuId") Long productSkuId);
}
