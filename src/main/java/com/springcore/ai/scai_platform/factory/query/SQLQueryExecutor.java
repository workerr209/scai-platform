package com.springcore.ai.scai_platform.factory.query;

import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
final class SQLQueryExecutor<T> implements QueryExecutor<T> {

    @NonNull
    private EntityManager em;

    @NonNull
    private RecordType recordType;

    @NonNull
    private MultiValueMap<String, String> param;

    @Override
    public List<T> execute() {

        final String customSelect = recordType.getCustomSelect();
        final String customFrom = recordType.getCustomFrom();
        final String customWhere = recordType.getCustomWhere();
        final String customGroup = recordType.getCustomGroup();
        final String customOrder = recordType.getCustomOrder();

        String sqlPattern = "select %s from %s";
        String sql = String.format(sqlPattern, customSelect, customFrom, customWhere);

        //boolean isAlreadyAddWhere = false;
        sql += " where 1 = 1 ";
        if(StringUtils.isNotBlank(customWhere)) {
            sql += " and " + customWhere;
            //isAlreadyAddWhere = true;
        }

        //whereFilter=name:goodName1,code:goodCode
        // String prop = recordType.getProp();

        final RecordTypePropertyProcessor recordTypePropertyProcessor = new RecordTypePropertyProcessor(recordType);
        final RecordTypePropertyProcessor.WhereFilter whereFilter = recordTypePropertyProcessor.getWhereFilter();
        final RecordTypePropertyProcessor.QueryMapping queryMapping = recordTypePropertyProcessor.getQueryMapping();
        final RecordTypePropertyProcessor.UsernameFilter usernameFilter = recordTypePropertyProcessor.getUsernameFilter();
        final RecordTypePropertyProcessor.ConJunction conJunction = recordTypePropertyProcessor.getConJunction();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final String filterCode = whereFilter.getCode();
        final String filterName = whereFilter.getName();
        if (!param.isEmpty()) {
            List<String> conJunctionParam = param.getOrDefault("conJunctionParam", Collections.emptyList());
            if (!conJunctionParam.isEmpty()) {
                param.remove("conJunctionParam");
            }

        	sql += " and " + param.entrySet().stream()
                    .map(o -> {
                        final List<String> values = o.getValue();
                        String value = values.get(0);
                        String key = o.getKey();
                        if(StringUtils.equals("code", key) && StringUtils.isNotBlank(filterCode)) {
                        	key = filterCode;
                        }
                        if(StringUtils.equals("name", key) && StringUtils.isNotBlank(filterName)) {
                        	key = filterName;
                        }
                        String operstor = StringUtils.contains(value, "%") ? " like " : " = ";
                        return key + operstor + ":" + key;
                    })
                    .collect(Collectors.joining(" and "));

            String whereConJunction = (conJunction != null) ? conJunction.getConJunction() : "";
            if (StringUtils.isNotEmpty(whereConJunction) && !conJunctionParam.isEmpty()) {
                if (!param.isEmpty()) {
                    whereConJunction = " and " + whereConJunction;
                }

                sql += whereConJunction;
                param.put("conJunctionParam", conJunctionParam);
            }
        }

        if(StringUtils.isNotBlank(customGroup)) {
            sql += " group by " + customGroup;
        }

        if(StringUtils.isNotBlank(customOrder)) {
            sql += " order by " + customOrder;
        }

        final Class<?> mappingClass = Utils.declareClassName(queryMapping.getFullClassName());
        if(mappingClass == null) {
            throw new RuntimeException("Class mapping not found : " + queryMapping.getFullClassName());
        }

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        final NativeQuery<?> nativeQuery = session.createNativeQuery(sql, mappingClass);

        addAllScalar(nativeQuery, mappingClass);

        param.forEach((key, value) -> {
            if (StringUtils.equals("code", key) && StringUtils.isNotBlank(filterCode)) {
                nativeQuery.setParameter(filterCode, value);
            } else if (StringUtils.equals("name", key) && StringUtils.isNotBlank(filterName)) {
                nativeQuery.setParameter(filterName, value);
            } else {
            	nativeQuery.setParameter(key, value);
            }
        });

        if(usernameFilter.getUsername() != null) {
        	nativeQuery.setParameter("username", username);
        }

        final List resultList = nativeQuery.getResultList();
        return resultList;

    }

    private void addAllScalar(NativeQuery nativeQuery, Class<?> resultClass) {
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            Class<?> propertyType = field.getType();

            if(propertyType.isAssignableFrom(String.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.STRING);
            } else if(propertyType.isAssignableFrom(Long.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.LONG);
            } else if(propertyType.isAssignableFrom(Integer.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.INTEGER);
            } else if(propertyType.isAssignableFrom(Double.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.DOUBLE);
            } else if(propertyType.isAssignableFrom(BigDecimal.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.BIG_DECIMAL);
            } else if(propertyType.isAssignableFrom(Date.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.DATE);
            } else if(propertyType.isAssignableFrom(Character.class)) {
                nativeQuery.addScalar(fieldName, StandardBasicTypes.CHARACTER);
            }
        }
    }

}
