package com.bj.wms.dto;

import com.bj.wms.entity.CreditRating;
import com.bj.wms.entity.CustomerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String customerCode;

    @NotBlank
    @Size(max = 100)
    private String customerName;

    private CustomerType customerType;

    private String contactPerson;

    private String contactPhone;

    private String email;

    private String address;

    private CreditRating creditRating;

    private BigDecimal creditLimit;

    private Boolean isEnabled;

    private Long createdTime;

    private Long updatedTime;
}


