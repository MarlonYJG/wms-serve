package com.bj.wms.repository;

import com.bj.wms.entity.InboundOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InboundOrderItemRepository extends JpaRepository<InboundOrderItem, Long>, JpaSpecificationExecutor<InboundOrderItem> {
    
    /**
     * 根据入库单ID删除所有明细
     */
    @Modifying
    @Query("DELETE FROM InboundOrderItem i WHERE i.inboundOrderId = :inboundOrderId")
    void deleteByInboundOrderId(@Param("inboundOrderId") Long inboundOrderId);
    
    /**
     * 根据商品SKU ID和收货数量大于0查询入库明细
     */
    List<InboundOrderItem> findByProductSkuIdAndReceivedQuantityGreaterThan(Long productSkuId, Integer receivedQuantity);
}


