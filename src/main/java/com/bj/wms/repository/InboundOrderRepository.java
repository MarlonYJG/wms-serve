package com.bj.wms.repository;

import com.bj.wms.entity.InboundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long>, JpaSpecificationExecutor<InboundOrder> {
    boolean existsByOrderNo(String orderNo);
    
    /**
     * 根据ID查询入库单详情，包含明细数据
     */
    @Query("SELECT o FROM InboundOrder o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<InboundOrder> findByIdWithItems(@Param("id") Long id);
}


