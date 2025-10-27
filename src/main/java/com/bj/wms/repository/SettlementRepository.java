package com.bj.wms.repository;

import com.bj.wms.entity.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 结算单Repository
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    /**
     * 根据结算单号查询
     */
    Optional<Settlement> findBySettlementNo(String settlementNo);

    /**
     * 分页查询结算单
     */
    @Query("SELECT s FROM Settlement s " +
           "LEFT JOIN FETCH s.customer " +
           "WHERE s.deleted = 0 " +
           "AND (:settlementNo IS NULL OR s.settlementNo LIKE %:settlementNo%) " +
           "AND (:customerId IS NULL OR s.customerId = :customerId) " +
           "AND (:status IS NULL OR s.status = :status) " +
           "AND (:startTime IS NULL OR s.periodStart >= :startTime) " +
           "AND (:endTime IS NULL OR s.periodEnd <= :endTime)")
    Page<Settlement> findSettlements(@Param("settlementNo") String settlementNo,
                                   @Param("customerId") Long customerId,
                                   @Param("status") Integer status,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   Pageable pageable);

    /**
     * 根据客户ID查询结算单
     */
    List<Settlement> findByCustomerIdAndDeletedFalse(Long customerId);

    /**
     * 根据状态查询结算单
     */
    List<Settlement> findByStatusAndDeletedFalse(Integer status);

    /**
     * 检查结算单号是否存在
     */
    boolean existsBySettlementNoAndDeletedFalse(String settlementNo);
    
    /**
     * 根据ID查询结算单（包含客户信息）
     */
    @Query("SELECT s FROM Settlement s LEFT JOIN FETCH s.customer WHERE s.id = :id")
    Optional<Settlement> findByIdWithCustomer(@Param("id") Long id);
}
