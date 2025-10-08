/*
 * @Author: Marlon
 * @Date: 2025-10-08 20:06:49
 * @Description: 
 */
package com.bj.wms.dto;

import lombok.Data;

@Data
public class StorageZoneQueryDTO {
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdTime";
    private String sortDir = "desc";

    private Long warehouseId;
    // 接收前端的字符串类型（如 STORAGE/RECEIVING/...）
    private String zoneType;
    private String keyword; // 按名称/编码模糊
    private Boolean isEnabled;
}


