package com.bj.wms.entity.converter;

import com.bj.wms.entity.LocationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class LocationTypeConverter implements AttributeConverter<LocationType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(LocationType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public LocationType convertToEntityAttribute(Integer dbData) {
        return LocationType.fromCode(dbData);
    }
}


