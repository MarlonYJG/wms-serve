package com.bj.wms.entity.converter;

import com.bj.wms.entity.AppointmentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AppointmentStatusConverter implements AttributeConverter<AppointmentStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AppointmentStatus attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public AppointmentStatus convertToEntityAttribute(Integer dbData) {
        return AppointmentStatus.fromCode(dbData);
    }
}


