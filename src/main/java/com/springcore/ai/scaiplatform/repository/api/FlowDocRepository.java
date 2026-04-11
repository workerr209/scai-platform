package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.FlowDoc;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlowDocRepository extends JpaRepository<FlowDoc, Long> {

    Optional<FlowDoc> findByDocId(Long docId);

    @Transactional
    void deleteByDocId(Long docId);

    boolean existsByDocId(Long docId);

}
