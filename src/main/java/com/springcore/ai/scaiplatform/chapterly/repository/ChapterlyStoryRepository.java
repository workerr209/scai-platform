package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterlyStoryRepository extends JpaRepository<ChapterlyStory, Long> {
    List<ChapterlyStory> findByOwnerIdOrderByUpdatedAtDesc(Long ownerId);

    Optional<ChapterlyStory> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    long countByOwnerId(Long ownerId);
}
