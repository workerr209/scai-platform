package com.springcore.ai.scaiplatform.repository.impl;

import com.springcore.ai.scaiplatform.repository.api.DynamicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class DynamicRepositoryImpl implements DynamicRepository {

    @PersistenceContext
    private EntityManager em;
    private static final String ENTITY_PACKAGE = "com.springcore.ai.scaiplatform.entity.";

    @Override
    public List<?> fetchData(String clazzLookup) {
        try {
            Class<?> clazz = Class.forName(ENTITY_PACKAGE + clazzLookup);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<?> cq = cb.createQuery(clazz);
            Root<?> root = cq.from(clazz);
            cq.where(cb.isNotNull(root.get("code")));
            cq.orderBy(cb.asc(root.get("code")));
            List<?> resultList = em.createQuery(cq).getResultList();
            log.info("DynamicRepository.fetchData ResultList Size : {}", resultList.size());
            return resultList;
        } catch (Exception e) {
            log.error("Failed DynamicRepository.fetchData", e);
            throw new RuntimeException("Not Found Entity : " + clazzLookup);
        }
    }

}