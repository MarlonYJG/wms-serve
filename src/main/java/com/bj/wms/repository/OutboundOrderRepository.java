package com.bj.wms.repository;

import com.bj.wms.entity.OutboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long>, JpaSpecificationExecutor<OutboundOrder> {
    
    boolean existsByOrderNo(String orderNo);
    
    Optional<OutboundOrder> findByOrderNo(String orderNo);
    
    @Query("SELECT o FROM OutboundOrder o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OutboundOrder> findByIdWithItems(@Param("id") Long id);
}