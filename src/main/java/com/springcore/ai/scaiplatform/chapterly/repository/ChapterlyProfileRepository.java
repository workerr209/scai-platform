package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterlyProfileRepository extends JpaRepository<ChapterlyProfile, Long> {
    Optional<ChapterlyProfile> findByUserId(Long userId);
}
