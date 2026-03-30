package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.domain.type.QueryType;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.entity.RecordTypeField;
import com.springcore.ai.scai_platform.service.api.DynamicClassService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.implementation.FieldAccessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DynamicClassServiceImpl implements DynamicClassService {
    private static final Map<String, Class<?>> DYNAMIC_MAPPING_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final Map<String, ByteArrayClassLoader> CLASS_LOADER_CACHE = new ConcurrentHashMap<>();

    @Override
    public Class<?> getMappingClass(RecordType recordType) {
        String recordTypeName = recordType.getName();
        return DYNAMIC_MAPPING_CACHE.computeIfAbsent(recordTypeName, key -> {
            log.info("SCAI: [Cache Miss] Generating NEW Dynamic Class for RecordType: {}", recordTypeName);
            ByteArrayClassLoader customLoader = new ByteArrayClassLoader(
                    getClass().getClassLoader(),
                    new HashMap<>()
            );
            CLASS_LOADER_CACHE.put(recordTypeName, customLoader);

            return buildMappingClass(recordType, customLoader);
        });
    }

    @Override
    public void removeMappingClass(String recordTypeName) {
        log.info("SCAI: Evicting Dynamic Class and Loader for: {}", recordTypeName);
        DYNAMIC_MAPPING_CACHE.remove(recordTypeName);
        CLASS_LOADER_CACHE.remove(recordTypeName);
    }

    private Class<?> buildMappingClass(RecordType recordType, ByteArrayClassLoader customLoader) {
        String recordTypeName = recordType.getName();
        boolean isSQL = QueryType.SQL.equals(recordType.getCustomQueryType());
        if (!isSQL) {
            return null;
        }

        try {
            String className = "com.springcore.ai.scai_platform.domain.mapping.dynamic." + recordType.getName();
            DynamicType.Builder<?> builder = new ByteBuddy()
                    .subclass(Object.class)
                    .name(className);

            String customSelect = recordType.getCustomSelect();
            String[] customSelectArr = StringUtils.split(customSelect, ",");
            List<RecordTypeField> fields = new ArrayList<>();
            recordType.getFieldList().forEach(field -> {
                String fldName = field.getName();
                if (isFieldInSelect(fldName, customSelectArr)) {
                    fields.add(field);
                }
            });

            log.info("BUILD MAPPING CLASS for RecordType: {} CustomSelect : {} FldName : {}", recordTypeName, customSelect, fields.stream().map(RecordTypeField::getName).collect(Collectors.joining(",")));
            for (RecordTypeField fld : fields) {
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
                    .load(customLoader)
                    .getLoaded();

        } catch (Exception e) {
            log.error("SCAI: Failed to generate dynamic class for {}", recordTypeName, e);
            throw new RuntimeException("Dynamic class generation failed", e);
        }
    }

    private boolean isFieldInSelect(String fieldName, String[] customSelectArr) {
        log.debug("isFieldInSelect : {}", fieldName);
        if (customSelectArr == null) {
            return false;
        }

        for (String sFld : customSelectArr) {
            if (StringUtils.isBlank(sFld)) continue;

            String trimmedFld = sFld.trim();
            String alias = "";

            String[] parts = trimmedFld.split("\\s+");
            if (parts.length > 0) {
                alias = parts[parts.length - 1];
            }

            if (alias.contains(".")) {
                alias = alias.substring(alias.lastIndexOf(".") + 1);
            }

            if (alias.equalsIgnoreCase(fieldName)) {
                return true;
            }
        }
        return false;
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
