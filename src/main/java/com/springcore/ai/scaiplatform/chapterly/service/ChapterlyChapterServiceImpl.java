package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.config.ChapterlyMessagingConfig;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyChapterStatus;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyChapterResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyChapterRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.messaging.ChapterlyEventPublisher;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterNoteRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingEntryRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingGoalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChapterlyChapterServiceImpl implements ChapterlyChapterService {

    private final ChapterlyChapterRepository chapterRepository;
    private final ChapterlyChapterNoteRepository noteRepository;
    private final ChapterlyWritingEntryRepository entryRepository;
    private final ChapterlyWritingGoalRepository goalRepository;
    private final ChapterlyOwnershipService ownershipService;
    private final ChapterlyEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyChapterResponse> listChapters(Long ownerUserId, Long storyId) {
        ownershipService.requireStory(storyId, ownerUserId);
        return chapterRepository.findByStoryIdAndOwnerIdOrderByChapterNumberAsc(storyId, ownerUserId)
                .stream()
                .map(ChapterlyChapterResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterlyChapterResponse getChapter(Long ownerUserId, Long storyId, Long chapterId) {
        return ChapterlyChapterResponse.from(ownershipService.requireChapter(storyId, chapterId, ownerUserId));
    }

    @Override
    @Transactional
    public ChapterlyChapterResponse createChapter(Long ownerUserId, Long storyId, CreateChapterlyChapterRequest request) {
        ChapterlyStory story = ownershipService.requireStory(storyId, ownerUserId);
        ChapterlyChapter chapter = ChapterlyChapter.builder()
                .story(story)
                .title(request.getTitle().trim())
                .chapterNumber(resolveChapterNumber(storyId, ownerUserId, request.getChapterNumber()))
                .status(ChapterlyChapterStatus.PLANNED)
                .targetWordCount(request.getTargetWordCount())
                .currentWordCount(0)
                .progressPercent(0)
                .build();
        chapter.setOwner(story.getOwner());

        return ChapterlyChapterResponse.from(chapterRepository.save(chapter));
    }

    @Override
    @Transactional
    public ChapterlyChapterResponse updateChapter(
            Long ownerUserId,
            Long storyId,
            Long chapterId,
            UpdateChapterlyChapterRequest request
    ) {
        ChapterlyChapter chapter = ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        ChapterlyChapterStatus previousStatus = chapter.getStatus();

        if (StringUtils.isNotBlank(request.getTitle())) {
            chapter.setTitle(request.getTitle().trim());
        }

        if (request.getChapterNumber() != null) {
            chapter.setChapterNumber(request.getChapterNumber());
        }

        if (request.getStatus() != null) {
            chapter.setStatus(request.getStatus());
        }

        if (request.getTargetWordCount() != null) {
            chapter.setTargetWordCount(request.getTargetWordCount());
        }

        if (request.getCurrentWordCount() != null) {
            applyWordCountDelta(chapter, request.getCurrentWordCount());
        }

        if (request.getBody() != null) {
            chapter.setBody(request.getBody());
        }

        chapter.setProgressPercent(calculateProgressPercent(chapter.getCurrentWordCount(), chapter.getTargetWordCount()));

        ChapterlyChapter saved = chapterRepository.save(chapter);
        publishChapterPublishedIfNeeded(ownerUserId, previousStatus, saved);

        return ChapterlyChapterResponse.from(saved);
    }

    @Override
    @Transactional
    public void deleteChapter(Long ownerUserId, Long storyId, Long chapterId) {
        ChapterlyChapter chapter = ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        applyWordCountDelta(chapter, 0);
        entryRepository.deleteByChapterIdAndOwnerId(chapterId, ownerUserId);
        goalRepository.deleteByChapterIdAndOwnerId(chapterId, ownerUserId);
        noteRepository.deleteByChapterIdAndOwnerId(chapterId, ownerUserId);
        chapterRepository.delete(chapter);
    }

    private Integer resolveChapterNumber(Long storyId, Long ownerUserId, Integer requestedChapterNumber) {
        return Objects.requireNonNullElseGet(requestedChapterNumber, () -> (int) chapterRepository.countByStoryIdAndOwnerId(storyId, ownerUserId) + 1);

    }

    private Integer calculateProgressPercent(Integer currentWordCount, Integer targetWordCount) {
        if (currentWordCount == null || targetWordCount == null || targetWordCount <= 0) {
            return 0;
        }

        return Math.min(100, (int) Math.round((currentWordCount * 100.0) / targetWordCount));
    }

    private void applyWordCountDelta(ChapterlyChapter chapter, Integer nextChapterWordCount) {
        int nextWordCount = defaultZero(nextChapterWordCount);
        chapter.setCurrentWordCount(nextWordCount);
        chapter.setProgressPercent(calculateProgressPercent(nextWordCount, chapter.getTargetWordCount()));
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private void publishChapterPublishedIfNeeded(Long ownerUserId, ChapterlyChapterStatus previousStatus, ChapterlyChapter chapter) {
        if (previousStatus == ChapterlyChapterStatus.PUBLISHED || chapter.getStatus() != ChapterlyChapterStatus.PUBLISHED) {
            return;
        }

        eventPublisher.publishAfterCommit(
                ChapterlyMessagingConfig.CHAPTER_PUBLISHED,
                ownerUserId,
                ownerUserId,
                "chapter",
                chapter.getId(),
                Map.of(
                        "storyId", chapter.getStory().getId(),
                        "title", chapter.getTitle()
                )
        );
    }
}
