package com.springcore.ai.scai_platform.factory.query;

import com.springcore.ai.scai_platform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.entity.RecordTypeField;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
class HQLQueryExecution<T> implements QueryExecutor<T> {

    @NonNull
    EntityManager em;
    @NonNull
    private RecordType recordType;

    @NonNull
    private MultiValueMap<String, String> param;

    @Override
    public List<T> execute() {
        return Optional.of(recordType)
                .map(recordType -> {
                    String classname = recordType.getClassName();
                    log.info("load dynamic class : {}", classname);
                    Class<T> clazz = null;
                    try {
                        clazz = (Class<T>) Class.forName(classname);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Map<String, String> configParam = param.keySet().stream()
                            .filter(key -> key.startsWith("config_"))
                            .collect(Collectors.toMap(x -> x, x -> param.get(x).get(0)));
                    configParam.keySet().forEach(key -> param.remove(key));

                    List<RecordTypeField> acRecordtypeFields = recordType.getRecordtypeFields();

                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<T> cq = cb.createQuery(clazz);

                    Root<?> from = cq.from(clazz);
                    
                    String prop = recordType.getProp();
                    String whereFilterCode = "";
                    
                    String whereFilterName = "";
//                    if(StringUtils.isNotEmpty(prop)) {
//                    	Arrays.stream(prop.split("[SEP.]")).map(string -> string.split("[SEP.]"))
//                    	 .filter(props -> "whereFilter".contains(props.toString()));
//                    }
//                    
                    String RoleValueMapping = "RoleValueMapping";
                    if(prop != null) {
                    	for(String props : prop.split("\\[SEP.]")) {
                    		if(props.startsWith("whereFilter")){
                    			for(String whereFilter : props.split("=")[1].split(",")) {
                    				if(whereFilter.startsWith("code")) {
                    					whereFilterCode = whereFilter.split(":")[1];
                    				} else if(whereFilter.startsWith("name")) {
                    					whereFilterName = whereFilter.split(":")[1];
                    				}
                    			}
                    		}else if(props.startsWith("RoleValueMapping")){
                    			RoleValueMapping = props.split("=")[1];
                    		}
                    	}
                    }
                    final String filterCode = whereFilterCode;
                    final String filterName = whereFilterName;

                    if (param != null) {
                        LinkedList<Predicate> predicates = new LinkedList<>();

                        param.forEach((k, lv) -> {
                        	
                        	if("code".equals(k) && !filterCode.isBlank()) {
                             	k = filterCode;
	                        }
	                        if("name".equals(k) && !filterName.isBlank()) {
	                         	k = filterName;
	                        }
                            Path<String> path = from.get(k);

                            
	                        final String key = k;
                            Class<? extends String> javaType = path.getJavaType();
                            boolean isInstanceOfPersistentObject = GenericPersistentObject.class.isAssignableFrom(javaType) || (Serializable.class.isAssignableFrom(javaType) && !javaType.equals(String.class));
                            
                            if(isInstanceOfPersistentObject) {
                                log.debug("{} is instanceOf {}", key, GenericPersistentObject.class);
                            }

                            Path<String> pathJoin = isInstanceOfPersistentObject ? path.get("id") : null;

                            lv.forEach(v -> {
                                String criteria = " = ";
                                Predicate pd = null;

                                if (v.indexOf("%") > -1) {
                                    pd = cb.like(pathJoin == null ? path : pathJoin, v);
                                } else {
                                    RecordTypeField recordTypeField = acRecordtypeFields.stream()
                                            .filter(obj -> key.equals(obj.getName()))
                                            .findFirst()
                                            .orElse(null);

                                    if (recordTypeField != null && StringUtils.hasLength(recordTypeField.getFilterOp())) {
                                        criteria = recordTypeField.getFilterOp();
                                    }
//
                                    if (criteria.equals("like")) {
                                        pd = cb.like(pathJoin == null ? path : pathJoin, "%" + v + "%");
                                    } else {
                                        final Object realValue;
                                        if(javaType.equals(Boolean.class)) {
                                            realValue = "1".equals(v) ? Boolean.TRUE : Boolean.FALSE;
                                        } else {
                                            realValue = v;
                                        }

                                        pd = cb.equal(pathJoin == null ? path : pathJoin, realValue);
                                    }

                                }

                                predicates.add(pd);

                            });
                        });

                        Predicate[] xs = predicates.toArray(new Predicate[predicates.size()]);

                        cq.where(cb.and(xs));
                    }

                    TypedQuery<T> query = em.createQuery(cq);
                    if(configParam != null && !configParam.isEmpty()) {
                        configParam.forEach((key, val) -> {
                            if(key.equals("config_maxresult")) {
                                query.setMaxResults(Integer.valueOf(val));
                            }
                        });
                    }

                    ArrayList<T> resultList = (ArrayList<T>) query.getResultList();
                    return  resultList;
                }).orElseGet(null);
    }
}
