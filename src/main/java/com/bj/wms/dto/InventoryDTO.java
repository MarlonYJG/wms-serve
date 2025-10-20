package com.bj.wms.dto;

import lombok.Data;

@Data
public class InventoryDTO {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private String batchNo;
    private String productionDate;
    private String expiryDate;
    private Integer quantity;
    private Integer lockedQuantity;
    private Integer availableQuantity;
    private Long createdTime;
}


