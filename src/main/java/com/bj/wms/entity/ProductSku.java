package com.bj.wms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品SKU 实体
 */
@Data
@Entity
@Table(name = "product_sku")
@EqualsAndHashCode(callSuper = true)
public class ProductSku extends BaseEntity {

    @NotBlank(message = "SKU编码不能为空")
    @Size(max = 100)
    @Column(name = "sku_code", nullable = false, unique = true, length = 100)
    private String skuCode;

    @NotBlank(message = "SKU名称不能为空")
    @Size(max = 255)
    @Column(name = "sku_name", nullable = false, length = 255)
    private String skuName;

    @Size(max = 255)
    @Column(name = "specification", length = 255)
    private String specification;

    @Size(max = 100)
    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Size(max = 100)
    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "volume")
    private BigDecimal volume;

    @Column(name = "is_batch_managed", columnDefinition = "BIT")
    private Boolean isBatchManaged = Boolean.FALSE;

    @Column(name = "is_expiry_managed", columnDefinition = "BIT")
    private Boolean isExpiryManaged = Boolean.FALSE;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "safety_stock")
    private Integer safetyStock;

    @Column(name = "is_enabled", columnDefinition = "BIT")
    private Boolean isEnabled = Boolean.TRUE;

    /**
     * 采购价格
     */
    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    /**
     * 成本价格（包含采购价和入库费用分摊）
     */
    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    /**
     * 销售价格
     */
    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    /**
     * 建议零售价
     */
    @Column(name = "retail_price", precision = 12, scale = 2)
    private BigDecimal retailPrice;
}


