package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.Document;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    /*@EntityGraph(attributePaths = {"employee", "reason"})
    Optional<Document> findByDocumentNo(String documentNo);

    @EntityGraph(attributePaths = {"employee", "reason"})
    List<Document> findByDocumentType(String documentType);

    @EntityGraph(attributePaths = {"employee", "reason"})
    List<Document> findByEmployee(Employee employee);*/

    @EntityGraph(attributePaths = {"employee", "reason"})
    @Query("""
    select d from Document d
    where d.documentType = :documentType
    and function('to_char', d.dateWork, 'YYYYMM') = :dateFormat
    order by d.id desc
    """)
    List<Document> findTopByTypeAndMonthOrderByIdDesc(
            String documentType,
            String dateFormat
    );

}