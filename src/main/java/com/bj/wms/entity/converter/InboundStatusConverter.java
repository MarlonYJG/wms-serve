package com.bj.wms.entity.converter;

import com.bj.wms.entity.InboundStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class InboundStatusConverter implements AttributeConverter<InboundStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(InboundStatus attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public InboundStatus convertToEntityAttribute(Integer dbData) {
        return InboundStatus.fromCode(dbData);
    }
}


