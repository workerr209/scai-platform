package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterlyChapterRepository extends JpaRepository<ChapterlyChapter, Long> {
    List<ChapterlyChapter> findByStoryIdAndOwnerIdOrderByChapterNumberAsc(Long storyId, Long ownerId);

    Optional<ChapterlyChapter> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<ChapterlyChapter> findByIdAndStoryIdAndOwnerId(Long id, Long storyId, Long ownerId);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    long countByStoryIdAndOwnerId(Long storyId, Long ownerId);

    void deleteByStoryIdAndOwnerId(Long storyId, Long ownerId);
}
