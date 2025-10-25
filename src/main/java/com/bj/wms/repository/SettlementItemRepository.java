package com.bj.wms.repository;

import com.bj.wms.entity.SettlementItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 结算明细Repository
 */
@Repository
public interface SettlementItemRepository extends JpaRepository<SettlementItem, Long> {

    /**
     * 根据结算单ID查询明细
     */
    List<SettlementItem> findBySettlementIdAndDeletedFalse(Long settlementId);

    /**
     * 根据出库单ID查询明细
     */
    List<SettlementItem> findByOutboundOrderIdAndDeletedFalse(Long outboundOrderId);

    /**
     * 检查出库单是否已在结算单中
     */
    @Query("SELECT COUNT(si) > 0 FROM SettlementItem si WHERE si.outboundOrderId = :outboundOrderId " +
           "AND si.settlementId = :settlementId AND si.deleted = 0")
    boolean existsByOutboundOrderIdAndSettlementId(@Param("outboundOrderId") Long outboundOrderId,
                                                  @Param("settlementId") Long settlementId);

    /**
     * 根据结算单ID删除明细
     */
    void deleteBySettlementId(Long settlementId);

    /**
     * 根据结算单ID和出库单ID删除明细
     */
    void deleteBySettlementIdAndOutboundOrderId(Long settlementId, Long outboundOrderId);
}
