package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterlyWritingGoalRepository extends JpaRepository<ChapterlyWritingGoal, Long> {
    List<ChapterlyWritingGoal> findByOwnerIdOrderByStartDateDesc(Long ownerId);

    List<ChapterlyWritingGoal> findByOwnerIdAndActiveTrueOrderByStartDateDesc(Long ownerId);

    List<ChapterlyWritingGoal> findByStoryIdAndOwnerIdAndActiveTrueOrderByStartDateDesc(Long storyId, Long ownerId);

    List<ChapterlyWritingGoal> findByChapterIdAndOwnerIdAndActiveTrueOrderByStartDateDesc(Long chapterId, Long ownerId);

    List<ChapterlyWritingGoal> findByOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            Long ownerId,
            LocalDate endDate,
            LocalDate startDate
    );

    Optional<ChapterlyWritingGoal> findByIdAndOwnerId(Long id, Long ownerId);
}
