package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapterNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterlyChapterNoteRepository extends JpaRepository<ChapterlyChapterNote, Long> {
    List<ChapterlyChapterNote> findByChapterIdAndOwnerIdOrderByPinnedDescUpdatedAtDesc(Long chapterId, Long ownerId);

    List<ChapterlyChapterNote> findTop5ByStoryIdAndOwnerIdOrderByUpdatedAtDesc(Long storyId, Long ownerId);

    Optional<ChapterlyChapterNote> findByIdAndOwnerId(Long id, Long ownerId);
}
