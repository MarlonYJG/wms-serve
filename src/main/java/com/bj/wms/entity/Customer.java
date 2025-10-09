package com.bj.wms.entity;

import com.bj.wms.entity.converter.CreditRatingConverter;
import com.bj.wms.entity.converter.CustomerTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "customer")
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "customer_code", nullable = false, unique = true, length = 50)
    private String customerCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Convert(converter = CustomerTypeConverter.class)
    @Column(name = "customer_type")
    private CustomerType customerType;

    @Size(max = 50)
    @Column(name = "contact_person", length = 50)
    private String contactPerson;

    @Size(max = 20)
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Convert(converter = CreditRatingConverter.class)
    @Column(name = "credit_rating")
    private CreditRating creditRating = CreditRating.C;

    @Column(name = "credit_limit")
    private BigDecimal creditLimit;

    @Column(name = "is_enabled")
    private Boolean isEnabled = Boolean.TRUE;
}


