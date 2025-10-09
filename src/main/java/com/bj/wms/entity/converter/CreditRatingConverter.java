package com.bj.wms.entity.converter;

import com.bj.wms.entity.CreditRating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CreditRatingConverter implements AttributeConverter<CreditRating, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CreditRating attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public CreditRating convertToEntityAttribute(Integer dbData) {
        return CreditRating.fromCode(dbData);
    }
}


