package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.InkQuest.InkDailyEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface InkDailyEntryRepository extends JpaRepository<InkDailyEntry, Long>, JpaSpecificationExecutor<InkDailyEntry> {

    List<InkDailyEntry> findByEmIdOrderByEntryDateDesc(Long emId);

    Optional<InkDailyEntry> findByIdAndEmId(Long id, Long emId);

    Optional<InkDailyEntry> findByEmIdAndEntryDate(Long emId, Date entryDate);

    List<InkDailyEntry> findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(Long emId, Date fromDate, Date toDate);

    List<InkDailyEntry> findByEmIdAndProjectIdOrderByEntryDateDesc(Long emId, Long projectId);

    List<InkDailyEntry> findByEmIdAndChapterIdOrderByEntryDateDesc(Long emId, Long chapterId);

    @Transactional
    void deleteByEmIdAndProjectId(Long emId, Long projectId);

    @Transactional
    void deleteByEmIdAndChapterId(Long emId, Long chapterId);
}
