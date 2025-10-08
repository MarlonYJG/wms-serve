package com.bj.wms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仓库数据传输对象
 * 
 * 用于API接口的数据传输，分离请求和响应数据结构
 */
@Data
public class WarehouseDTO {
    
    private Long id;
    
    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 50, message = "仓库编码长度不能超过50个字符")
    private String code;
    
    @NotBlank(message = "仓库名称不能为空")
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
    
    private BigDecimal usedCapacity;
    
    // 时间字段使用时间戳（毫秒）
    private Long createdTime;
    
    private Long updatedTime;
    
    private String createdBy;
    
    private String updatedBy;
}
