package com.bj.wms.entity.converter;

import com.bj.wms.entity.ZoneType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ZoneTypeConverter implements AttributeConverter<ZoneType, Byte> {

    @Override
    public Byte convertToDatabaseColumn(ZoneType attribute) {
        return attribute == null ? null : (byte) attribute.getCode();
    }

    @Override
    public ZoneType convertToEntityAttribute(Byte dbData) {
        return ZoneType.fromCode(dbData == null ? null : dbData.intValue());
    }
}


