package com.bj.wms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户DTO
 */
@Data
public class CustomerDTO {
    private Long id;
    private String customerCode;
    private String customerName;
    private String customerType;
    private String contactPerson;
    private String contactPhone;
    private String email;
    private String address;
    private String creditRating;
    private BigDecimal creditLimit;
    private Boolean isEnabled;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}