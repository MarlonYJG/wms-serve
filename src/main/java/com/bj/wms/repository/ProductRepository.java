package com.bj.wms.repository;

import com.bj.wms.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商品数据访问层
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据商品编码查找商品
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * 检查商品编码是否存在
     */
    boolean existsByProductCode(String productCode);

    /**
     * 根据商品名称模糊查询
     */
    Page<Product> findByProductNameContaining(String productName, Pageable pageable);

    /**
     * 根据分类查找商品
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * 根据品牌查找商品
     */
    Page<Product> findByBrand(String brand, Pageable pageable);

    /**
     * 根据状态查找商品
     */
    Page<Product> findByStatus(Integer status, Pageable pageable);

    /**
     * 查找库存不足的商品（库存数量 <= 最低库存）
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStock AND p.deleted = 0")
    List<Product> findLowStockProducts();

    /**
     * 查找库存过多的商品（库存数量 >= 最高库存）
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity >= p.maxStock AND p.deleted = 0")
    List<Product> findOverStockProducts();

    /**
     * 根据关键词搜索商品（商品名称、编码、描述）
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(p.productName LIKE %:keyword% OR " +
           "p.productCode LIKE %:keyword% OR " +
           "p.description LIKE %:keyword%) AND " +
           "p.deleted = 0")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查找未删除的商品
     */
    @Query("SELECT p FROM Product p WHERE p.deleted = 0")
    Page<Product> findAllActive(Pageable pageable);

    /**
     * 根据价格范围查找商品
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.deleted = 0")
    Page<Product> findByPriceRange(@Param("minPrice") Double minPrice, 
                                   @Param("maxPrice") Double maxPrice, 
                                   Pageable pageable);
}


