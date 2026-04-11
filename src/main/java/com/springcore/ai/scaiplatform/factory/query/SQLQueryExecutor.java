package com.springcore.ai.scaiplatform.factory.query;

import com.springcore.ai.scaiplatform.entity.RecordType;
import com.springcore.ai.scaiplatform.properties.ApplicationProperties;
import com.springcore.ai.scaiplatform.service.api.DynamicClassService;
import com.springcore.ai.scaiplatform.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
final class SQLQueryExecutor<T> implements QueryExecutor<T> {

    @NonNull private final ApplicationProperties applicationProperties;
    @NonNull private final DynamicClassService dynamicClassService;
    @NonNull private final EntityManager em;
    @NonNull private final RecordType recordType;
    @NonNull private final MultiValueMap<String, String> param;

    @Override
    @SuppressWarnings({"unchecked", "SqlSourceToSinkFlow"})
    public List<T> execute() {
        RecordTypePropertyProcessor processor = new RecordTypePropertyProcessor(recordType);
        RecordTypePropertyProcessor.QueryMapping queryMapping = processor.getQueryMapping();
        String fullClassName = queryMapping.getFullClassName();

        Class<?> mappingClass = null;
        if (StringUtils.isNotBlank(fullClassName)) {
            mappingClass = Utils.declareClassName(fullClassName);
        }

        if (mappingClass == null) {
            mappingClass = dynamicClassService.getMappingClass(recordType);
        }

        if (mappingClass == null) {
            log.error("Mapping class not found: {}", fullClassName);
            throw new RuntimeException("Class mapping not found : " + fullClassName);
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(recordType.getCustomSelect())
                .append(" FROM ").append(recordType.getCustomFrom())
                .append(" WHERE 1 = 1 ");
        if (StringUtils.isNotBlank(recordType.getCustomWhere())) {
            sql.append(" AND (").append(recordType.getCustomWhere()).append(") ");
        }

        appendDynamicFilters(sql);
        List<String> conJunctionParam = param.remove("conJunctionParam");
        appendConjunction(sql, processor, conJunctionParam);

        if (StringUtils.isNotBlank(recordType.getCustomGroup())) {
            sql.append(" GROUP BY ").append(recordType.getCustomGroup());
        }
        if (StringUtils.isNotBlank(recordType.getCustomOrder())) {
            sql.append(" ORDER BY ").append(recordType.getCustomOrder());
        }

        log.debug("Executing Native RecordTypeName: {}, SQL: {} ", recordType.getName(), sql);
        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        NativeQuery<T> nativeQuery = session.createNativeQuery(sql.toString(), (Class<T>) mappingClass);
        addAllScalar(nativeQuery, mappingClass);

        Class<?> finalMappingClass = mappingClass;
        nativeQuery.setTupleTransformer((tuples, aliases) -> {
            try {
                T entity = (T) finalMappingClass.getDeclaredConstructor().newInstance();

                for (int i = 0; i < aliases.length; i++) {
                    String alias = aliases[i];
                    Object value = tuples[i];
                    if (value != null) {
                        setFieldValue(entity, alias, value);
                    }
                }
                return entity;
            } catch (Exception e) {
                log.error("SCAI Mapping Error: {}", e.getMessage());
                return null;
            }
        });

        bindParameters(nativeQuery, processor, sql.toString(), conJunctionParam);

        try {
            return nativeQuery.getResultList();
        } catch (Exception e) {
            log.error("SQLQueryExecutor nativeQuery.getResultList Error: {}", e.getMessage());
            throw e;
        }
    }

    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            // ลองหา Field ตรงๆ ก่อน
            Field field = findField(target.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);

                // จัดการเรื่อง Data Type mismatch เบื้องต้น (เช่น BigDecimal -> Long)
                Object convertedValue = convertType(value, field.getType());
                field.set(target, convertedValue);
            }
        } catch (Exception e) {
            log.trace("Could not map field {}: {}", fieldName, e.getMessage());
        }
    }

    private Field findField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.getName().equalsIgnoreCase(name)) return f;
            }
        }
        return null;
    }

    private Object convertType(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;

        // แปลง BigDecimal (จาก Hibernate) เป็น Type ที่เราต้องการใน Java
        if (value instanceof Number num) {
            if (targetType == Long.class || targetType == long.class) return num.longValue();
            if (targetType == Integer.class || targetType == int.class) return num.intValue();
            if (targetType == Double.class || targetType == double.class) return num.doubleValue();
        }
        return value;
    }

    private Object parseIsoDate(String isoString, String userTimezone) {
        if (StringUtils.isBlank(isoString)) return null;
        try {
            java.time.Instant instant = java.time.OffsetDateTime.parse(isoString).toInstant();
            java.time.ZoneId zoneId = java.time.ZoneId.of(userTimezone != null ? userTimezone : "Asia/Bangkok");
            java.time.LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
            return java.sql.Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            log.warn("Failed to parse date with timezone: {}, error: {}", isoString, e.getMessage());
            return isoString;
        }
    }

    private void appendDynamicFilters(StringBuilder sql) {
        if (param.isEmpty()) return;

        param.keySet().forEach(key -> {
            List<String> values = param.get(key);
            if (values == null || values.isEmpty() || StringUtils.isBlank(values.get(0))) return;

            // ค้นหา Metadata ของฟิลด์นี้
            var fldMetadata = recordType.getFieldList().stream()
                    .filter(f -> f.getName().equals(key))
                    .findFirst();

            if (fldMetadata.isPresent() && StringUtils.isNotBlank(fldMetadata.get().getFilterField())) {
                String dbColumnName = fldMetadata.get().getFilterField();
                String operator = values.get(0).contains("%") ? " LIKE " : " = ";
                sql.append(" AND ").append(dbColumnName).append(operator).append(":").append(key);
            }
        });
    }

    private void appendConjunction(StringBuilder sql, RecordTypePropertyProcessor processor, List<String> conjParam) {
        RecordTypePropertyProcessor.ConJunction conj = processor.getConJunction();
        if (conj != null && StringUtils.isNotEmpty(conj.getConJunction()) && conjParam != null && !conjParam.isEmpty()) {
            sql.append(" AND (").append(conj.getConJunction()).append(") ");
        }
    }

    private void bindParameters(NativeQuery<?> query, RecordTypePropertyProcessor processor, String sql, List<String> conjParam) {
        String userTimezone = applicationProperties.getGeneral().getDefaultUserPreference().getTimezone();
        List<String> processedKeys = new ArrayList<>();
        recordType.getFieldList().stream()
                .filter(fld -> "DATE".equals(fld.getDataType()))
                .forEach(fld -> {
                    String fldName = fld.getName();
                    if (param.containsKey(fldName)) {
                        String rawValue = param.getFirst(fldName);
                        if (StringUtils.isNotBlank(rawValue)) {
                            Object parsedValue = parseIsoDate(rawValue, userTimezone);
                            query.setParameter(fldName, parsedValue);
                            processedKeys.add(fldName); // มาร์คไว้ว่าจัดการแล้ว
                        }
                    }
                });
        processedKeys.forEach(param::remove);

        param.forEach((key, values) -> {
            if (values != null && !values.isEmpty() && sql.contains(":" + key)) {
                query.setParameter(key, values.get(0));
            }
        });

        if (conjParam != null && !conjParam.isEmpty()) {
            query.setParameter("conJunctionParam", conjParam);
        }

        log.debug("Todo parameters for query: {}", processor);
        /*RecordTypePropertyProcessor.UsernameFilter userFilter = processor.getUsernameFilter();
        if (userFilter != null && userFilter.getUsername() != null) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            query.setParameter("username", currentUsername);
            log.debug("Binding username filter: {}", currentUsername);
        }*/
    }

    private void addAllScalar(NativeQuery<?> query, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            String name = field.getName();
            Class<?> type = field.getType();

            try {
                if (type.isAssignableFrom(String.class)) {
                    query.addScalar(name, StandardBasicTypes.STRING);
                } else if (type.isAssignableFrom(Long.class) || type == long.class) {
                    query.addScalar(name, StandardBasicTypes.LONG);
                } else if (type.isAssignableFrom(Integer.class) || type == int.class) {
                    query.addScalar(name, StandardBasicTypes.INTEGER);
                } else if (type.isAssignableFrom(BigDecimal.class)) {
                    query.addScalar(name, StandardBasicTypes.BIG_DECIMAL);
                } else if (type.isAssignableFrom(Date.class)) {
                    query.addScalar(name, StandardBasicTypes.DATE);
                } else if (type.isAssignableFrom(Boolean.class) || type == boolean.class) {
                    query.addScalar(name, StandardBasicTypes.BOOLEAN);
                } else if (type.isAssignableFrom(Double.class) || type == double.class) {
                    query.addScalar(name, StandardBasicTypes.DOUBLE);
                }

                log.trace("SCAI: Successfully added scalar for {}", name);

            } catch (Exception e) {
                log.warn("SCAI: Skip scalar mapping for field '{}' (Not found in SQL SELECT)", name);
            }
        }
    }

}