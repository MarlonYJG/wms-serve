package com.bj.wms.entity.converter;

import com.bj.wms.entity.LocationStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class LocationStatusConverter implements AttributeConverter<LocationStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(LocationStatus attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public LocationStatus convertToEntityAttribute(Integer dbData) {
        return LocationStatus.fromCode(dbData);
    }
}


