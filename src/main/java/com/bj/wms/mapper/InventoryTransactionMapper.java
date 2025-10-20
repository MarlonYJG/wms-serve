package com.bj.wms.mapper;

import com.bj.wms.dto.InventoryTransactionDTO;
import com.bj.wms.entity.InventoryTransaction;

public final class InventoryTransactionMapper {
    private InventoryTransactionMapper() {}

    public static InventoryTransactionDTO toDTO(InventoryTransaction e, String warehouseName, String locationCode, String skuCode, String productName) {
        if (e == null) return null;
        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setId(e.getId());
        dto.setProductSkuId(e.getProductSkuId());
        dto.setProductName(productName);
        dto.setSkuCode(skuCode);
        dto.setBatchNo(e.getBatchNo());
        dto.setWarehouseId(e.getWarehouseId());
        dto.setWarehouseName(warehouseName);
        dto.setLocationId(e.getLocationId());
        dto.setLocationCode(locationCode);
        dto.setTransactionType(e.getTransactionType());
        dto.setTransactionTypeName(switch (e.getTransactionType()) {
            case 1 -> "入库";
            case 2 -> "出库";
            case 3 -> "移库";
            case 4 -> "调整";
            default -> "未知";
        });
        dto.setRelatedOrderNo(e.getRelatedOrderNo());
        dto.setQuantityChange(e.getQuantityChange());
        dto.setQuantityAfter(e.getQuantityAfter());
        if (e.getTransactionTime() != null) {
            dto.setTransactionTime(e.getTransactionTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        dto.setOperator(e.getOperator());
        return dto;
    }
}


