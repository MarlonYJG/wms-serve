package com.bj.wms.dto;

import lombok.Data;

@Data
public class InventoryCountItemDTO {
    private Long id;
    private Long countId;
    private Long locationId;
    private String locationCode;
    private Long productSkuId;
    private String productName;
    private String skuCode;
    private String batchNo;
    private Integer systemQty;
    private Integer countedQty;
    private Integer differenceQty;
}


