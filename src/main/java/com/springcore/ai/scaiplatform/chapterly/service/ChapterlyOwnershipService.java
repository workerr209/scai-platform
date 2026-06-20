package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyStoryRepository;
import com.springcore.ai.scaiplatform.core.entity.User;
import com.springcore.ai.scaiplatform.core.repository.api.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChapterlyOwnershipService {

    private final UserRepository userRepository;
    private final ChapterlyStoryRepository storyRepository;
    private final ChapterlyChapterRepository chapterRepository;

    @Transactional(readOnly = true)
    public User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Authenticated user was not found"));
    }

    @Transactional(readOnly = true)
    public ChapterlyStory requireStory(Long storyId, Long ownerUserId) {
        return storyRepository.findByIdAndOwnerId(storyId, ownerUserId)
                .orElseThrow(() -> new ValidationException("Story was not found for this user"));
    }

    @Transactional(readOnly = true)
    public ChapterlyChapter requireChapter(Long chapterId, Long ownerUserId) {
        return chapterRepository.findByIdAndOwnerId(chapterId, ownerUserId)
                .orElseThrow(() -> new ValidationException("Chapter was not found for this user"));
    }

    @Transactional(readOnly = true)
    public ChapterlyChapter requireChapter(Long storyId, Long chapterId, Long ownerUserId) {
        return chapterRepository.findByIdAndStoryIdAndOwnerId(chapterId, storyId, ownerUserId)
                .orElseThrow(() -> new ValidationException("Chapter was not found for this story and user"));
    }
}
