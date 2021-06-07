package com.runmate.domain.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GradeConverter implements AttributeConverter<Grade,String> {

    @Override
    public String convertToDatabaseColumn(Grade grade) {
        return grade==null ? Grade.of("UNRANKED").getValue()  : grade.getValue();
    }

    @Override
    public Grade convertToEntityAttribute(String dbData) {
        return Grade.of(dbData);
    }
}
