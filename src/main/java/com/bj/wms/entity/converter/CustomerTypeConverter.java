package com.bj.wms.entity.converter;

import com.bj.wms.entity.CustomerType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CustomerTypeConverter implements AttributeConverter<CustomerType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CustomerType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public CustomerType convertToEntityAttribute(Integer dbData) {
        return CustomerType.fromCode(dbData);
    }
}


