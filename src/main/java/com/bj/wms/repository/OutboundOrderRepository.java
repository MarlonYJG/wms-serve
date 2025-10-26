package com.bj.wms.repository;

import com.bj.wms.entity.OutboundOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long>, JpaSpecificationExecutor<OutboundOrder> {
    
    boolean existsByOrderNo(String orderNo);
    
    Optional<OutboundOrder> findByOrderNo(String orderNo);
    
    @EntityGraph(attributePaths = {"warehouse", "customer", "items"})
    @Query("SELECT o FROM OutboundOrder o WHERE o.id = :id")
    Optional<OutboundOrder> findByIdWithItems(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = {"warehouse", "customer"})
    Page<OutboundOrder> findAll(Specification<OutboundOrder> spec, Pageable pageable);
    
    List<OutboundOrder> findByStatus(Integer status);
    
    /**
     * 根据客户ID和状态查询出库单
     */
    @Query("SELECT o FROM OutboundOrder o WHERE o.customerId = :customerId AND o.status = :status " +
           "AND o.id NOT IN (SELECT si.outboundOrderId FROM SettlementItem si WHERE si.deleted = 0)")
    List<OutboundOrder> findByCustomerIdAndStatusAndSettledFalse(@Param("customerId") Long customerId, 
                                                                @Param("status") Integer status);
}