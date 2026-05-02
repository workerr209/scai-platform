package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.domain.constant.InkQuest.InkChapterStatus;
import com.springcore.ai.scaiplatform.entity.InkQuest.InkChapter;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InkChapterRepository extends JpaRepository<InkChapter, Long>, JpaSpecificationExecutor<InkChapter> {

    List<InkChapter> findByEmIdAndProjectIdOrderByChapterNoAsc(Long emId, Long projectId);

    Optional<InkChapter> findByIdAndEmId(Long id, Long emId);

    Optional<InkChapter> findFirstByEmIdAndProjectIdAndStatusOrderByChapterNoAsc(
            Long emId,
            Long projectId,
            InkChapterStatus status
    );

    long countByEmIdAndProjectId(Long emId, Long projectId);

    long countByEmIdAndProjectIdAndStatus(Long emId, Long projectId, InkChapterStatus status);

    boolean existsByIdAndEmId(Long id, Long emId);

    @Transactional
    void deleteByIdAndEmId(Long id, Long emId);

    @Transactional
    void deleteByEmIdAndProjectId(Long emId, Long projectId);
}
