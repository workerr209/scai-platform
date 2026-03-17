package com.springcore.ai.scai_platform.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.springcore.ai.scai_platform.domain.type.CustomProperty;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class CustomPropertyAttributeConverter implements AttributeConverter<CustomProperty, String> {

    final JsonMapper jsonMapper = JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Override
    public String convertToDatabaseColumn(CustomProperty attribute) {
        if (attribute == null)
            return null;

        try {
            return jsonMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting CustomProperty attribute to json", e);
            return null;
        }
    }

    @Override
    public CustomProperty convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return new CustomProperty();

        if (dbData.trim().startsWith("{") || dbData.trim().startsWith("[")) {
            try {
                return jsonMapper.convertValue(dbData, CustomProperty.class);
            } catch (Exception e) {
                log.warn("Invalid JSON structure, returning raw data: {}", dbData);
            }
        }

        return null;
    }
}
