package com.bj.wms.repository;

import com.bj.wms.entity.ChargeDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 费用字典Repository
 */
@Repository
public interface ChargeDictRepository extends JpaRepository<ChargeDict, Long> {

    /**
     * 根据费用编码查询
     */
    Optional<ChargeDict> findByChargeCodeAndDeletedFalse(String chargeCode);

    /**
     * 查询所有启用的费用字典
     */
    @Query(value = "SELECT * FROM charge_dict cd WHERE cd.is_enabled = 1 AND cd.deleted = 0 ORDER BY cd.charge_code", nativeQuery = true)
    List<ChargeDict> findByIsEnabledTrueAndDeletedFalseOrderByChargeCode();

    /**
     * 查询所有费用字典
     */
    List<ChargeDict> findByDeletedFalseOrderByChargeCode();

    /**
     * 检查费用编码是否存在
     */
    boolean existsByChargeCodeAndDeletedFalse(String chargeCode);

    /**
     * 根据费用名称模糊查询
     */
    @Query(value = "SELECT * FROM charge_dict cd WHERE cd.deleted = 0 " +
           "AND (:chargeName IS NULL OR cd.charge_name LIKE CONCAT('%', :chargeName, '%')) " +
           "AND (:isEnabled IS NULL OR cd.is_enabled = :isEnabled)", nativeQuery = true)
    List<ChargeDict> findChargeDicts(@Param("chargeName") String chargeName,
                                   @Param("isEnabled") Boolean isEnabled);
}
