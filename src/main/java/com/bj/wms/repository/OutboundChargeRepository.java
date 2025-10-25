package com.bj.wms.repository;

import com.bj.wms.entity.OutboundCharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库费用Repository
 */
@Repository
public interface OutboundChargeRepository extends JpaRepository<OutboundCharge, Long> {

    /**
     * 根据出库单ID查询费用
     */
    List<OutboundCharge> findByOutboundOrderIdAndDeletedFalse(Long outboundOrderId);

    /**
     * 分页查询出库费用
     */
    @Query("SELECT oc FROM OutboundCharge oc WHERE oc.deleted = 0 " +
           "AND (:outboundOrderId IS NULL OR oc.outboundOrderId = :outboundOrderId) " +
           "AND (:chargeType IS NULL OR oc.chargeType = :chargeType) " +
           "AND (:startTime IS NULL OR oc.createdTime >= :startTime) " +
           "AND (:endTime IS NULL OR oc.createdTime <= :endTime)")
    Page<OutboundCharge> findCharges(@Param("outboundOrderId") Long outboundOrderId,
                                   @Param("chargeType") Long chargeType,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   Pageable pageable);

    /**
     * 根据出库单ID和费用类型查询
     */
    List<OutboundCharge> findByOutboundOrderIdAndChargeTypeAndDeletedFalse(Long outboundOrderId, Long chargeType);

    /**
     * 根据出库单ID删除费用
     */
    void deleteByOutboundOrderId(Long outboundOrderId);

    /**
     * 计算出库单费用总额
     */
    @Query("SELECT COALESCE(SUM(oc.amount), 0) FROM OutboundCharge oc WHERE oc.outboundOrderId = :outboundOrderId AND oc.deleted = 0")
    Double sumAmountByOutboundOrderId(@Param("outboundOrderId") Long outboundOrderId);
}
