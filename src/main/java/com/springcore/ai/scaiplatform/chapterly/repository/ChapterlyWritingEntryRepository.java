package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterlyWritingEntryRepository extends JpaRepository<ChapterlyWritingEntry, Long> {
    List<ChapterlyWritingEntry> findByOwnerIdOrderByEntryDateDesc(Long ownerId);

    List<ChapterlyWritingEntry> findByOwnerIdAndEntryDateBetweenOrderByEntryDateDesc(
            Long ownerId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<ChapterlyWritingEntry> findByStoryIdAndOwnerIdOrderByEntryDateDesc(Long storyId, Long ownerId);

    List<ChapterlyWritingEntry> findByChapterIdAndOwnerIdOrderByEntryDateDesc(Long chapterId, Long ownerId);

    Optional<ChapterlyWritingEntry> findByIdAndOwnerId(Long id, Long ownerId);
}
