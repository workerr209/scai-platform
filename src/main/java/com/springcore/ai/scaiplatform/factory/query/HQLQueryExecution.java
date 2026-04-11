package com.springcore.ai.scaiplatform.factory.query;

import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.entity.RecordType;
import com.springcore.ai.scaiplatform.entity.RecordTypeField;
import com.springcore.ai.scaiplatform.service.api.DynamicClassService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
class HQLQueryExecution<T> implements QueryExecutor<T> {

    @NonNull private final DynamicClassService dynamicClassService;
    @NonNull private final EntityManager em;
    @NonNull private final RecordType recordType;
    @NonNull private final MultiValueMap<String, String> param;

    @Override
    @SuppressWarnings("unchecked")
    public List<T> execute() {
        String className = recordType.getClassName();
        try {
            Class<T> clazz;
            if (StringUtils.isBlank(className)) {
                clazz = (Class<T>) dynamicClassService.getMappingClass(recordType);
            } else {
                clazz = (Class<T>) Class.forName(className);
            }

            log.info("Loading dynamic class: {}", className);

            Map<String, String> configParam = extractConfigParams();
            Map<String, String> filterMetadata = parseMetadata(recordType.getProp());

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clazz);
            Root<T> root = cq.from(clazz);
            List<Predicate> predicates = new ArrayList<>();

            buildPredicates(root, cb, predicates, filterMetadata);
            if (!predicates.isEmpty()) {
                cq.where(cb.and(predicates.toArray(new Predicate[0])));
            }

            TypedQuery<T> query = em.createQuery(cq);
            applyConfigs(query, configParam);
            return query.getResultList();

        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", className);
            throw new RuntimeException("Dynamic class loading failed", e);
        }
    }

    private Map<String, String> extractConfigParams() {
        Map<String, String> configs = new HashMap<>();
        List<String> keysToRemove = new ArrayList<>();

        param.forEach((key, values) -> {
            if (key.startsWith("config_") && !values.isEmpty()) {
                configs.put(key, values.get(0));
                keysToRemove.add(key);
            }
        });
        keysToRemove.forEach(param::remove);
        return configs;
    }

    private Map<String, String> parseMetadata(String prop) {
        Map<String, String> metadata = new HashMap<>();
        if (!StringUtils.isNotBlank(prop)) return metadata;

        // แยกด้วย [SEP.]
        String[] parts = prop.split("\\[SEP.]");
        for (String part : parts) {
            if (part.startsWith("whereFilter=")) {
                String val = part.split("=")[1];
                for (String entry : val.split(",")) {
                    String[] kv = entry.split(":");
                    if (kv.length == 2) metadata.put("filter_" + kv[0], kv[1]);
                }
            } else if (part.startsWith("RoleValueMapping=")) {
                metadata.put("roleMapping", part.split("=")[1]);
            }
        }
        return metadata;
    }

    private void buildPredicates(Root<T> root, CriteriaBuilder cb, List<Predicate> predicates, Map<String, String> meta) {
        param.forEach((key, values) -> {
            // Mapping key ตาม metadata
            String targetKey = key;
            if ("code".equals(key)) targetKey = meta.getOrDefault("filter_code", key);
            if ("name".equals(key)) targetKey = meta.getOrDefault("filter_name", key);

            try {
                Path<?> path = root.get(targetKey);
                Class<?> javaType = path.getJavaType();

                // เช็คว่าเป็น Persistent Object หรือไม่ (คล้าย Logic เดิม)
                boolean isEntity = GenericPersistentObject.class.isAssignableFrom(javaType) ||
                        (Serializable.class.isAssignableFrom(javaType) && !javaType.equals(String.class));

                final Path<?> finalPath = isEntity ? path.get("id") : path;
                final String finalKey = targetKey;
                values.forEach(val -> predicates.add(createPredicate(finalPath, cb, val, finalKey, javaType)));
            } catch (Exception e) {
                log.warn("Field {} not found in entity, skipping filter.", targetKey);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Predicate createPredicate(Path<?> path, CriteriaBuilder cb, String value, String key, Class<?> javaType) {
        // หา Filter Operation จาก RecordTypeField
        String op = recordType.getFieldList().stream()
                .filter(f -> key.equals(f.getName()))
                .map(RecordTypeField::getFilterOp)
                .findFirst()
                .orElse("=");

        if (value.contains("%") || "like".equalsIgnoreCase(op)) {
            String pattern = value.contains("%") ? value : "%" + value + "%";
            return cb.like((Path<String>) path, pattern);
        }

        if (javaType.equals(Boolean.class)) {
            return cb.equal(path, "1".equals(value));
        }

        return cb.equal(path, value);
    }

    private void applyConfigs(TypedQuery<T> query, Map<String, String> configs) {
        if (configs.containsKey("config_maxresult")) {
            query.setMaxResults(Integer.parseInt(configs.get("config_maxresult")));
        }
    }
}