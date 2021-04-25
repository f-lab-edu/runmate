package com.runmate.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GradeConverter implements AttributeConverter<Grade,Character> {

    @Override
    public Character convertToDatabaseColumn(Grade grade) {
        return grade.getValue();
    }

    @Override
    public Grade convertToEntityAttribute(Character dbData) {
        return Grade.of(dbData);
    }
}
