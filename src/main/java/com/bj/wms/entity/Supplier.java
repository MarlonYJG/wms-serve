package com.bj.wms.entity;

import com.bj.wms.entity.converter.SupplierRatingConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "supplier")
@EqualsAndHashCode(callSuper = true)
public class Supplier extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "supplier_code", nullable = false, unique = true, length = 50)
    private String supplierCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "supplier_name", nullable = false, length = 100)
    private String supplierName;

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

    @Convert(converter = SupplierRatingConverter.class)
    @Column(name = "rating")
    private SupplierRating rating = SupplierRating.C;

    @Column(name = "is_enabled")
    private Boolean isEnabled = Boolean.TRUE;
}


