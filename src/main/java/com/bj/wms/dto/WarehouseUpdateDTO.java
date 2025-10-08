package com.bj.wms.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仓库更新请求DTO
 */
@Data
public class WarehouseUpdateDTO {
    
    @Size(max = 200, message = "仓库名称长度不能超过200个字符")
    private String name;
    
    @Size(max = 300, message = "仓库地址长度不能超过300个字符")
    private String address;
    
    @Size(max = 50, message = "联系人长度不能超过50个字符")
    private String contactPerson;
    
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String contactPhone;
    
    private Boolean isEnabled;
    
    private BigDecimal totalCapacity;
}
