package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.domain.type.QueryType;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.entity.RecordTypeField;
import com.springcore.ai.scai_platform.service.api.DynamicClassService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DynamicClassServiceImpl implements DynamicClassService {
    private static final Map<String, Class<?>> DYNAMIC_MAPPING_CACHE = new ConcurrentHashMap<>();

    @Override
    public Class<?> getMappingClass(RecordType recordType) {
        String recordTypeName = recordType.getName();
        return DYNAMIC_MAPPING_CACHE.computeIfAbsent(recordTypeName, key -> {
            log.info("SCAI: [Cache Miss] Generating NEW Dynamic Class for RecordType: {}", recordTypeName);
            return buildMappingClass(recordType);
        });
    }

    @Override
    public void reloadMappingClass(RecordType recordType) {
        DYNAMIC_MAPPING_CACHE.put(recordType.getName(), getMappingClass(recordType));
    }

    private Class<?> buildMappingClass(RecordType recordType) {
        String recordTypeName = recordType.getName();
        boolean isSQL = QueryType.SQL.equals(recordType.getCustomQueryType());

        try {
            DynamicType.Builder<?> builder = new ByteBuddy()
                    .subclass(Object.class)
                    .name("com.springcore.ai.scai_platform.domain.mapping.dynamic." + recordTypeName);

            List<RecordTypeField> fields = recordType.getRecordtypeFields()
                    .stream()
                    .filter(f -> {
                        if (isSQL) {
                            return f.getDisplaySeq() != null && f.getDisplaySeq() != 0;
                        }

                        return true;
                    })
                    .toList();

            Class<Long> TYPE_ID = Long.class;
            String fldID = "id";
            builder = builder.defineField(fldID, TYPE_ID, Visibility.PRIVATE)
                    .defineMethod("get" + StringUtils.capitalize(fldID), TYPE_ID, Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofField(fldID))
                    .defineMethod("set" + StringUtils.capitalize(fldID), void.class, Visibility.PUBLIC)
                    .withParameter(TYPE_ID)
                    .intercept(FieldAccessor.ofField(fldID));

            for (RecordTypeField fld : fields) {
                if ("id".equals(fld.getName())) {
                    continue;
                }

                Class<?> javaType = determineJavaType(fld.getDataType());
                String fieldName = fld.getName();

                builder = builder.defineField(fieldName, javaType, Visibility.PRIVATE)
                        .defineMethod("get" + StringUtils.capitalize(fieldName), javaType, Visibility.PUBLIC)
                        .intercept(FieldAccessor.ofField(fieldName))
                        .defineMethod("set" + StringUtils.capitalize(fieldName), void.class, Visibility.PUBLIC)
                        .withParameter(javaType)
                        .intercept(FieldAccessor.ofField(fieldName));
            }

            return builder.make()
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

        } catch (Exception e) {
            log.error("SCAI: Failed to generate dynamic class for {}", recordTypeName, e);
            throw new RuntimeException("Dynamic class generation failed", e);
        }
    }

    private Class<?> determineJavaType(String scaiDataType) {
        if (StringUtils.isBlank(scaiDataType)) {
            return String.class;
        }

        return switch (scaiDataType.toUpperCase()) {
            case "INTEGER", "SELECTINT", "SPINNER", "COLNO", "CHECKBOX" -> Integer.class;
            case "LONG", "ID", "RECORD" -> Long.class;
            case "DECIMAL" -> java.math.BigDecimal.class;
            case "DATE", "YEAR", "YEARMONTH", "DATETIME", "TIMESTAMP" -> java.util.Date.class;
            case "TIME" -> java.sql.Time.class;
            default -> String.class;
        };
    }

}
