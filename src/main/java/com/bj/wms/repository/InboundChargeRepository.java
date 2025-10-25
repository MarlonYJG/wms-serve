package com.bj.wms.repository;

import com.bj.wms.entity.InboundCharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 入库费用Repository
 */
@Repository
public interface InboundChargeRepository extends JpaRepository<InboundCharge, Long> {

    /**
     * 根据入库单ID查询费用
     */
    @Query("SELECT ic FROM InboundCharge ic " +
           "LEFT JOIN FETCH ic.inboundOrder " +
           "LEFT JOIN FETCH ic.chargeDict " +
           "WHERE ic.inboundOrderId = :inboundOrderId AND ic.deleted = 0")
    List<InboundCharge> findByInboundOrderIdAndDeletedFalse(@Param("inboundOrderId") Long inboundOrderId);

    /**
     * 分页查询入库费用
     */
    @Query("SELECT ic FROM InboundCharge ic " +
           "LEFT JOIN FETCH ic.inboundOrder " +
           "LEFT JOIN FETCH ic.chargeDict " +
           "WHERE ic.deleted = 0 " +
           "AND (:inboundOrderId IS NULL OR ic.inboundOrderId = :inboundOrderId) " +
           "AND (:inboundOrderNo IS NULL OR ic.inboundOrder.orderNo LIKE %:inboundOrderNo%) " +
           "AND (:chargeType IS NULL OR ic.chargeType = :chargeType) " +
           "AND (:startTime IS NULL OR ic.createdTime >= :startTime) " +
           "AND (:endTime IS NULL OR ic.createdTime <= :endTime)")
    Page<InboundCharge> findCharges(@Param("inboundOrderId") Long inboundOrderId,
                                   @Param("inboundOrderNo") String inboundOrderNo,
                                   @Param("chargeType") Long chargeType,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   Pageable pageable);

    /**
     * 根据入库单ID和费用类型查询费用
     */
    @Query("SELECT ic FROM InboundCharge ic " +
           "LEFT JOIN FETCH ic.inboundOrder " +
           "LEFT JOIN FETCH ic.chargeDict " +
           "WHERE ic.inboundOrderId = :inboundOrderId AND ic.chargeType = :chargeType AND ic.deleted = 0")
    List<InboundCharge> findByInboundOrderIdAndChargeTypeAndDeletedFalse(@Param("inboundOrderId") Long inboundOrderId,
                                                                         @Param("chargeType") Long chargeType);
}
