package com.bj.wms.dto;

import lombok.Data;

/**
 * 仓库查询请求DTO
 */
@Data
public class WarehouseQueryDTO {
    
    private Integer page = 1;
    
    private Integer size = 10;
    
    private String sortBy = "createdTime";
    
    private String sortDir = "desc";
    
    private String keyword;
    
    private String name;
    
    private String code;
    
    private Boolean isEnabled;
    
    // 时间范围查询（时间戳）
    private Long startTime;
    
    private Long endTime;
}
