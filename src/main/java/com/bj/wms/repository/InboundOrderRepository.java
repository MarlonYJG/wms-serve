package com.bj.wms.repository;

import com.bj.wms.entity.InboundOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long>, JpaSpecificationExecutor<InboundOrder> {
    boolean existsByOrderNo(String orderNo);
    
    /**
     * 根据ID查询入库单详情，包含明细数据
     */
    @Query("SELECT o FROM InboundOrder o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<InboundOrder> findByIdWithItems(@Param("id") Long id);

    /**
     * 查询入库单列表，包含关联的warehouse和supplier信息
     */
    @Query("SELECT o FROM InboundOrder o LEFT JOIN FETCH o.warehouse LEFT JOIN FETCH o.supplier")
    List<InboundOrder> findAllWithAssociations();

    /**
     * 重写findAll方法，使用EntityGraph加载关联信息
     */
    @Override
    @EntityGraph(attributePaths = {"warehouse", "supplier"})
    List<InboundOrder> findAll();

    /**
     * 重写findAll方法，使用EntityGraph加载关联信息（带Specification和Pageable）
     */
    @Override
    @EntityGraph(attributePaths = {"warehouse", "supplier"})
    Page<InboundOrder> findAll(Specification<InboundOrder> spec, Pageable pageable);
}


