package com.bj.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSkuDTO {
    private Long id;

    @NotBlank(message = "SKU编码不能为空")
    @Size(max = 100)
    private String skuCode;

    @NotBlank(message = "SKU名称不能为空")
    @Size(max = 255)
    private String skuName;

    private String specification;

    private String brand;

    private Long categoryId;

    private Long supplierId;

    private String barcode;

    private BigDecimal weight;

    private BigDecimal volume;

    private Boolean isBatchManaged;

    private Boolean isExpiryManaged;

    private Integer shelfLifeDays;

    private Integer safetyStock;

    private Boolean isEnabled;

    /**
     * 采购价格
     */
    private BigDecimal purchasePrice;

    /**
     * 成本价格（包含采购价和入库费用分摊）
     */
    private BigDecimal costPrice;

    /**
     * 销售价格
     */
    private BigDecimal salePrice;

    /**
     * 建议零售价
     */
    private BigDecimal retailPrice;

    private Long createdTime;

    private Long updatedTime;
}


