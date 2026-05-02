package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.InkQuest.InkProject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InkProjectRepository extends JpaRepository<InkProject, Long>, JpaSpecificationExecutor<InkProject> {

    List<InkProject> findByEmIdOrderByUpdatedAtDesc(Long emId);

    Optional<InkProject> findByIdAndEmId(Long id, Long emId);

    long countByEmId(Long emId);

    boolean existsByIdAndEmId(Long id, Long emId);

    @Transactional
    void deleteByIdAndEmId(Long id, Long emId);
}
