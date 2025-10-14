package com.bj.wms.entity.converter;

import com.bj.wms.entity.QcStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class QcStatusConverter implements AttributeConverter<QcStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(QcStatus attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public QcStatus convertToEntityAttribute(Integer dbData) {
        return QcStatus.fromCode(dbData);
    }
}


