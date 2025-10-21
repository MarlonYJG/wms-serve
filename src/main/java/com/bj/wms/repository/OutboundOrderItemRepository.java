package com.bj.wms.repository;

import com.bj.wms.entity.OutboundOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OutboundOrderItemRepository extends JpaRepository<OutboundOrderItem, Long> {
    
    List<OutboundOrderItem> findByOutboundOrderId(Long outboundOrderId);
    
    @Query("SELECT oi FROM OutboundOrderItem oi WHERE oi.outboundOrderId = :outboundOrderId AND oi.productSkuId = :productSkuId")
    Optional<OutboundOrderItem> findByOutboundOrderIdAndProductSkuId(@Param("outboundOrderId") Long outboundOrderId, @Param("productSkuId") Long productSkuId);
    
    void deleteByOutboundOrderId(Long outboundOrderId);
}