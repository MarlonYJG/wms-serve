package com.bj.wms.mapper;

import com.bj.wms.dto.CustomerDTO;
import com.bj.wms.entity.Customer;

public final class CustomerMapper {
    private CustomerMapper() {}

    public static CustomerDTO toDTO(Customer entity) {
        if (entity == null) return null;
        CustomerDTO dto = new CustomerDTO();
        dto.setId(entity.getId());
        dto.setCustomerCode(entity.getCustomerCode());
        dto.setCustomerName(entity.getCustomerName());
        dto.setCustomerType(entity.getCustomerType());
        dto.setContactPerson(entity.getContactPerson());
        dto.setContactPhone(entity.getContactPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setCreditRating(entity.getCreditRating());
        dto.setCreditLimit(entity.getCreditLimit());
        dto.setIsEnabled(entity.getIsEnabled());
        if (entity.getCreatedTime() != null) {
            dto.setCreatedTime(entity.getCreatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (entity.getUpdatedTime() != null) {
            dto.setUpdatedTime(entity.getUpdatedTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return dto;
    }

    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) return null;
        Customer entity = new Customer();
        entity.setId(dto.getId());
        entity.setCustomerCode(dto.getCustomerCode());
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerType(dto.getCustomerType());
        entity.setContactPerson(dto.getContactPerson());
        entity.setContactPhone(dto.getContactPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        entity.setCreditRating(dto.getCreditRating());
        entity.setCreditLimit(dto.getCreditLimit());
        entity.setIsEnabled(dto.getIsEnabled() == null ? Boolean.TRUE : dto.getIsEnabled());
        return entity;
    }
}


