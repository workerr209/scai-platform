package com.springcore.ai.scai_platform.domain.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Converter
@Slf4j
public class QueryTypeAttributeConverter implements AttributeConverter<QueryType, String> {

    @Override
    public String convertToDatabaseColumn(QueryType attribute) {
        if (attribute == null)
            return null;

        switch (attribute) {
            case SQL:
                return "SQL";
            case HQL:
                return "HQL";
            default:
                throw new IllegalArgumentException(attribute + " not supported.");
        }
    }

    @Override
    public QueryType convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData))
            return null;

        return switch (dbData) {
            case "SQL" -> QueryType.SQL;
            case "HQL" -> QueryType.HQL;
            default -> {
                log.warn("convertToEntityAttribute {} not supported.", dbData);
                yield QueryType.NONE;
            }
        };
    }
}
