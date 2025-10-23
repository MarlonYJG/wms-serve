package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 包装材料DTO
 */
@Data
public class PackingMaterialDTO {
    private Long id;
    private String materialCode;
    private String materialName;
    private Integer materialType;
    private String materialTypeName;
    private String specification;
    private BigDecimal unitPrice;
    private String unit;
    private Boolean isEnabled;
    private String remark;
}
