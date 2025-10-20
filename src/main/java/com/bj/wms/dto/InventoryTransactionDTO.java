package com.bj.wms.dto;

import lombok.Data;

@Data
public class InventoryTransactionDTO {
    private Long id;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private String batchNo;
    private Long warehouseId;
    private String warehouseName;
    private Long locationId;
    private String locationCode;
    private Integer transactionType;
    private String transactionTypeName;
    private String relatedOrderNo;
    private Integer quantityChange;
    private Integer quantityAfter;
    private Long transactionTime;
    private Integer operator;
    private String operatorName;
}


