package com.bj.wms.dto;

import lombok.Data;

@Data
public class InventoryDTO {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private Long zoneId;
    private String zoneCode;
    private String zoneName;
    private String zoneType;
    private String zoneTypeName;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private String batchNo;
    private String productionDate;
    private String expiryDate;
    private Integer quantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private String createdTime;
    
    // 商品价格信息
    private java.math.BigDecimal purchasePrice;
    private java.math.BigDecimal costPrice;
    private java.math.BigDecimal salePrice;
    private java.math.BigDecimal retailPrice;
    
    // 入库费用信息
    private java.math.BigDecimal inboundCharges;
    private java.math.BigDecimal unitInboundCharges;
    
    // 成本计算
    private java.math.BigDecimal totalCost;
    private java.math.BigDecimal unitTotalCost;
}


