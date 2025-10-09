package com.bj.wms.dto;

import com.bj.wms.entity.SupplierRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String supplierCode;

    @NotBlank
    @Size(max = 100)
    private String supplierName;

    private String contactPerson;

    private String contactPhone;

    private String email;

    private String address;

    private SupplierRating rating;

    private Boolean isEnabled;

    private Long createdTime;

    private Long updatedTime;
}


