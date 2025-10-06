package com.bj.wms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品实体类
 * 
 * 仓库管理系统中的商品信息
 */
@Data
@Entity
@Table(name = "products")
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @NotBlank(message = "商品编码不能为空")
    @Size(max = 50, message = "商品编码长度不能超过50个字符")
    @Column(name = "product_code", unique = true, nullable = false, length = 50)
    private String productCode;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Size(max = 500, message = "商品描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    @NotBlank(message = "商品规格不能为空")
    @Size(max = 100, message = "商品规格长度不能超过100个字符")
    @Column(name = "specification", nullable = false, length = 100)
    private String specification;

    @NotBlank(message = "商品单位不能为空")
    @Size(max = 20, message = "商品单位长度不能超过20个字符")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "商品价格必须大于0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @NotNull(message = "最低库存不能为空")
    @Column(name = "min_stock", nullable = false)
    private Integer minStock = 0;

    @NotNull(message = "最高库存不能为空")
    @Column(name = "max_stock", nullable = false)
    private Integer maxStock = 1000;

    /**
     * 商品状态
     * 0: 下架, 1: 上架
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 商品分类
     */
    @Size(max = 100, message = "商品分类长度不能超过100个字符")
    @Column(name = "category", length = 100)
    private String category;

    /**
     * 商品品牌
     */
    @Size(max = 100, message = "商品品牌长度不能超过100个字符")
    @Column(name = "brand", length = 100)
    private String brand;
}


