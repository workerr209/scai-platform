package com.springcore.ai.scaiplatform.core.repository.impl;

import com.springcore.ai.scaiplatform.core.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.core.entity.Document;
import com.springcore.ai.scaiplatform.core.repository.api.DocumentRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class DocumentRepositoryCustomImpl implements DocumentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Document> searchByCriteria(DocumentSearchReq criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Document> query = cb.createQuery(Document.class);
        Root<Document> root = query.from(Document.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        if (criteria.getEmId() != null) {
            predicates.add(cb.equal(root.get("emId"), criteria.getEmId()));
        }

        if (criteria.getDocumentStatus() != null && !criteria.getDocumentStatus().isEmpty()) {
            predicates.add(cb.equal(root.get("documentStatus"), criteria.getDocumentStatus()));
        }

        if (criteria.getDateVF() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dateWork"), criteria.getDateVF()));
        }
        if (criteria.getDateVT() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dateWork"), criteria.getDateVT()));
        }

        query.where(cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
        query.orderBy(cb.desc(root.get("id")));
        return em.createQuery(query).getResultList();
    }
}