package com.bj.wms.entity.converter;

import com.bj.wms.entity.SupplierRating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SupplierRatingConverter implements AttributeConverter<SupplierRating, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SupplierRating attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public SupplierRating convertToEntityAttribute(Integer dbData) {
        return SupplierRating.fromCode(dbData);
    }
}


