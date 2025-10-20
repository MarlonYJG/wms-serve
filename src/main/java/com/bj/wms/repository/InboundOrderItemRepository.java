package com.bj.wms.repository;

import com.bj.wms.entity.InboundOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InboundOrderItemRepository extends JpaRepository<InboundOrderItem, Long>, JpaSpecificationExecutor<InboundOrderItem> {
    
    /**
     * 根据入库单ID删除所有明细
     */
    @Modifying
    @Query("DELETE FROM InboundOrderItem i WHERE i.inboundOrderId = :inboundOrderId")
    void deleteByInboundOrderId(@Param("inboundOrderId") Long inboundOrderId);
}


