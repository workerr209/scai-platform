package com.springcore.ai.scai_platform.factory.query;

import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.properties.ApplicationProperties;
import com.springcore.ai.scai_platform.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @NonNull private final EntityManager em;
    @NonNull private final RecordType recordType;
    @NonNull private final MultiValueMap<String, String> param;

    @Override
    @SuppressWarnings({"unchecked", "SqlSourceToSinkFlow"})
    public List<T> execute() {
        RecordTypePropertyProcessor processor = new RecordTypePropertyProcessor(recordType);
        RecordTypePropertyProcessor.QueryMapping queryMapping = processor.getQueryMapping();

        Class<?> mappingClass = Utils.declareClassName(queryMapping.getFullClassName());
        if (mappingClass == null) {
            log.error("Mapping class not found: {}", queryMapping.getFullClassName());
            throw new RuntimeException("Class mapping not found : " + queryMapping.getFullClassName());
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(recordType.getCustomSelect())
                .append(" FROM ").append(recordType.getCustomFrom())
                .append(" WHERE 1 = 1 ");
        if (StringUtils.isNotBlank(recordType.getCustomWhere())) {
            sql.append(" AND (").append(recordType.getCustomWhere()).append(") ");
        }
        appendDynamicFilters(sql, processor);

        List<String> conJunctionParam = param.remove("conJunctionParam");
        appendConjunction(sql, processor, conJunctionParam);

        if (StringUtils.isNotBlank(recordType.getCustomGroup())) {
            sql.append(" GROUP BY ").append(recordType.getCustomGroup());
        }
        if (StringUtils.isNotBlank(recordType.getCustomOrder())) {
            sql.append(" ORDER BY ").append(recordType.getCustomOrder());
        }

        log.debug("Executing Native SQL: {}", sql);
        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        NativeQuery<T> nativeQuery = session.createNativeQuery(sql.toString(), (Class<T>) mappingClass);
        addAllScalar(nativeQuery, mappingClass);
        bindParameters(nativeQuery, processor, conJunctionParam);
        return nativeQuery.getResultList();
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

    private void appendDynamicFilters(StringBuilder sql, RecordTypePropertyProcessor processor) {
        if (param.isEmpty()) return;

        RecordTypePropertyProcessor.WhereFilter whereFilter = processor.getWhereFilter();
        param.keySet().forEach(key -> {
            List<String> values = param.get(key);
            if (values == null || values.isEmpty() || StringUtils.isBlank(values.get(0))) return;

            String dbColumnName = key;
            if ("code".equals(key) && StringUtils.isNotBlank(whereFilter.getCode())) dbColumnName = whereFilter.getCode();
            if ("name".equals(key) && StringUtils.isNotBlank(whereFilter.getName())) dbColumnName = whereFilter.getName();

            String operator = values.get(0).contains("%") ? " LIKE " : " = ";
            sql.append(" AND ").append(dbColumnName).append(operator).append(":").append(key);
        });
    }

    private void appendConjunction(StringBuilder sql, RecordTypePropertyProcessor processor, List<String> conjParam) {
        RecordTypePropertyProcessor.ConJunction conj = processor.getConJunction();
        if (conj != null && StringUtils.isNotEmpty(conj.getConJunction()) && conjParam != null && !conjParam.isEmpty()) {
            sql.append(" AND (").append(conj.getConJunction()).append(") ");
        }
    }

    private void bindParameters(NativeQuery<?> query, RecordTypePropertyProcessor processor, List<String> conjParam) {
        String userTimezone = applicationProperties.getGeneral().getDefaultUserPreference().getTimezone();
        List<String> processedKeys = new ArrayList<>();
        recordType.getRecordtypeFields().stream()
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

        // 3. Bind Parameter ที่เหลือ (String, Long, ฯลฯ)
        param.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                query.setParameter(key, values.get(0));
            }
        });

        // 4. Bind Conjunction และ Username
        if (conjParam != null && !conjParam.isEmpty()) {
            query.setParameter("conJunctionParam", conjParam);
        }

        RecordTypePropertyProcessor.UsernameFilter userFilter = processor.getUsernameFilter();
        if (userFilter != null && userFilter.getUsername() != null) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            query.setParameter("username", currentUsername);
            log.debug("Binding username filter: {}", currentUsername);
        }
    }

    /**
     * Mapping ฟิลด์ใน Class Java เข้ากับประเภทข้อมูลของ SQL Standard
     */
    private void addAllScalar(NativeQuery<?> query, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> type = field.getType();
            String name = field.getName();

            if (type.isAssignableFrom(String.class)) {
                query.addScalar(name, StandardBasicTypes.STRING);
            } else if (type.isAssignableFrom(Long.class) || type == long.class) {
                query.addScalar(name, StandardBasicTypes.LONG);
            } else if (type.isAssignableFrom(Integer.class) || type == int.class) {
                query.addScalar(name, StandardBasicTypes.INTEGER);
            } else if (type.isAssignableFrom(Double.class) || type == double.class) {
                query.addScalar(name, StandardBasicTypes.DOUBLE);
            } else if (type.isAssignableFrom(BigDecimal.class)) {
                query.addScalar(name, StandardBasicTypes.BIG_DECIMAL);
            } else if (type.isAssignableFrom(Date.class)) {
                query.addScalar(name, StandardBasicTypes.DATE);
            } else if (type.isAssignableFrom(Character.class) || type == char.class) {
                query.addScalar(name, StandardBasicTypes.CHARACTER);
            } else if (type.isAssignableFrom(Boolean.class) || type == boolean.class) {
                query.addScalar(name, StandardBasicTypes.BOOLEAN);
            }
        }
    }
}