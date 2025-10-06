package com.bj.wms.repository;

import com.bj.wms.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 仓库数据访问层
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    /**
     * 根据仓库编码查找仓库
     */
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    /**
     * 检查仓库编码是否存在
     */
    boolean existsByWarehouseCode(String warehouseCode);

    /**
     * 根据仓库名称模糊查询
     */
    Page<Warehouse> findByWarehouseNameContaining(String warehouseName, Pageable pageable);

    /**
     * 根据状态查找仓库
     */
    Page<Warehouse> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据地址模糊查询
     */
    Page<Warehouse> findByAddressContaining(String address, Pageable pageable);

    /**
     * 根据负责人查找仓库
     */
    Page<Warehouse> findByManager(String manager, Pageable pageable);

    /**
     * 查找未删除的仓库
     */
    @Query("SELECT w FROM Warehouse w WHERE w.deleted = 0")
    Page<Warehouse> findAllActive(Pageable pageable);

    /**
     * 根据关键词搜索仓库（名称、编码、地址）
     */
    @Query("SELECT w FROM Warehouse w WHERE " +
           "(w.warehouseName LIKE %:keyword% OR " +
           "w.warehouseCode LIKE %:keyword% OR " +
           "w.address LIKE %:keyword%) AND " +
           "w.deleted = 0")
    Page<Warehouse> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找容量使用率超过指定百分比的仓库
     */
    @Query("SELECT w FROM Warehouse w WHERE " +
           "w.capacity > 0 AND " +
           "(w.usedCapacity / w.capacity) > :threshold AND " +
           "w.deleted = 0")
    List<Warehouse> findWarehousesWithHighUsage(@Param("threshold") Double threshold);

    /**
     * 查找启用状态的仓库
     */
    @Query("SELECT w FROM Warehouse w WHERE w.status = 1 AND w.deleted = 0")
    List<Warehouse> findActiveWarehouses();
}


