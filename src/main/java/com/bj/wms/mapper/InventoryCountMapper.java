package com.bj.wms.mapper;

import com.bj.wms.dto.InventoryCountDTO;
import com.bj.wms.dto.InventoryCountItemDTO;
import com.bj.wms.entity.InventoryCount;
import com.bj.wms.entity.InventoryCountItem;

import java.util.List;

public final class InventoryCountMapper {
    private InventoryCountMapper() {}

    public static InventoryCountDTO toDTO(InventoryCount e, String warehouseName, List<InventoryCountItemDTO> items) {
        if (e == null) return null;
        InventoryCountDTO d = new InventoryCountDTO();
        d.setId(e.getId());
        d.setCountNo(e.getCountNo());
        d.setWarehouseId(e.getWarehouseId());
        d.setWarehouseName(warehouseName);
        d.setStatus(e.getStatus());
        d.setStatusName(statusName(e.getStatus()));
        d.setRemark(e.getRemark());
        if (e.getCreatedTime() != null) {
            d.setCreatedTime(e.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        d.setItems(items);
        return d;
    }

    public static InventoryCountItemDTO toItemDTO(InventoryCountItem it, String locationCode, String skuCode, String productName) {
        if (it == null) return null;
        InventoryCountItemDTO d = new InventoryCountItemDTO();
        d.setId(it.getId());
        d.setCountId(it.getCountId());
        d.setLocationId(it.getLocationId());
        d.setLocationCode(locationCode);
        d.setProductSkuId(it.getProductSkuId());
        d.setSkuCode(skuCode);
        d.setProductName(productName);
        d.setBatchNo(it.getBatchNo());
        d.setSystemQty(it.getSystemQty());
        d.setCountedQty(it.getCountedQty());
        d.setDifferenceQty(it.getDifferenceQty());
        return d;
    }

    private static String statusName(Integer s) {
        return switch (s == null ? 0 : s) {
            case 1 -> "草稿";
            case 2 -> "进行中";
            case 3 -> "已提交";
            case 4 -> "已完成";
            default -> "未知";
        };
    }
}


