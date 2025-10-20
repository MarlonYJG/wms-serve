package com.bj.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class InventoryCountDTO {
    private Long id;
    private String countNo;
    private Long warehouseId;
    private String warehouseName;
    private Integer status;
    private String statusName;
    private String remark;
    private Long createdTime;
    private List<InventoryCountItemDTO> items;
}


