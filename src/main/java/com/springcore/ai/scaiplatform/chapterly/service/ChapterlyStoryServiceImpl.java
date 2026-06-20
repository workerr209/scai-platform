package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyAudienceRating;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryStatus;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryVisibility;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyStoryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyStoryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyStoryRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyStoryRepository;
import com.springcore.ai.scaiplatform.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChapterlyStoryServiceImpl implements ChapterlyStoryService {

    private final ChapterlyStoryRepository storyRepository;
    private final ChapterlyOwnershipService ownershipService;

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyStoryResponse> listStories(Long ownerUserId) {
        return storyRepository.findByOwnerIdOrderByUpdatedAtDesc(ownerUserId)
                .stream()
                .map(ChapterlyStoryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterlyStoryResponse getStory(Long ownerUserId, Long storyId) {
        return ChapterlyStoryResponse.from(ownershipService.requireStory(storyId, ownerUserId));
    }

    @Override
    @Transactional
    public ChapterlyStoryResponse createStory(Long ownerUserId, CreateChapterlyStoryRequest request) {
        User owner = ownershipService.requireUser(ownerUserId);
        ChapterlyStory story = ChapterlyStory.builder()
                .title(request.getTitle().trim())
                .penName(trimToNull(request.getPenName()))
                .genre(trimToNull(request.getGenre()))
                .tags(sanitizeTags(request.getTags()))
                .language(defaultIfNull(request.getLanguage(), ChapterlyLanguage.EN))
                .audienceRating(defaultIfNull(request.getAudienceRating(), ChapterlyAudienceRating.TEEN))
                .summary(request.getSummary())
                .coverFileId(request.getCoverFileId())
                .coverUrl(request.getCoverUrl())
                .status(defaultIfNull(request.getStatus(), ChapterlyStoryStatus.DRAFT))
                .visibility(defaultIfNull(request.getVisibility(), ChapterlyStoryVisibility.PRIVATE))
                .targetWordCount(request.getTargetWordCount())
                .dailyWordTarget(request.getDailyWordTarget())
                .currentWordCount(0)
                .progressPercent(0)
                .privateNote(request.getPrivateNote())
                .build();
        story.setOwner(owner);

        return ChapterlyStoryResponse.from(storyRepository.save(story));
    }

    @Override
    @Transactional
    public ChapterlyStoryResponse updateStory(Long ownerUserId, Long storyId, UpdateChapterlyStoryRequest request) {
        ChapterlyStory story = ownershipService.requireStory(storyId, ownerUserId);

        if (StringUtils.isNotBlank(request.getTitle())) {
            story.setTitle(request.getTitle().trim());
        }

        if (request.getSummary() != null) {
            story.setSummary(request.getSummary());
        }

        if (request.getPenName() != null) {
            story.setPenName(trimToNull(request.getPenName()));
        }

        if (request.getGenre() != null) {
            story.setGenre(trimToNull(request.getGenre()));
        }

        if (request.getTags() != null) {
            story.setTags(sanitizeTags(request.getTags()));
        }

        if (request.getLanguage() != null) {
            story.setLanguage(request.getLanguage());
        }

        if (request.getAudienceRating() != null) {
            story.setAudienceRating(request.getAudienceRating());
        }

        if (request.getCoverFileId() != null) {
            story.setCoverFileId(request.getCoverFileId());
        }

        if (request.getCoverUrl() != null) {
            story.setCoverUrl(request.getCoverUrl());
        }

        if (request.getStatus() != null) {
            story.setStatus(request.getStatus());
        }

        if (request.getVisibility() != null) {
            story.setVisibility(request.getVisibility());
        }

        if (request.getTargetWordCount() != null) {
            story.setTargetWordCount(request.getTargetWordCount());
        }

        if (request.getDailyWordTarget() != null) {
            story.setDailyWordTarget(request.getDailyWordTarget());
        }

        if (request.getCurrentWordCount() != null) {
            story.setCurrentWordCount(request.getCurrentWordCount());
        }

        if (request.getPrivateNote() != null) {
            story.setPrivateNote(request.getPrivateNote());
        }

        story.setProgressPercent(calculateProgressPercent(story.getCurrentWordCount(), story.getTargetWordCount()));

        return ChapterlyStoryResponse.from(storyRepository.save(story));
    }

    @Override
    @Transactional
    public void deleteStory(Long ownerUserId, Long storyId) {
        ChapterlyStory story = ownershipService.requireStory(storyId, ownerUserId);
        storyRepository.delete(story);
    }

    private Integer calculateProgressPercent(Integer currentWordCount, Integer targetWordCount) {
        if (currentWordCount == null || targetWordCount == null || targetWordCount <= 0) {
            return 0;
        }

        return Math.min(100, (int) Math.round((currentWordCount * 100.0) / targetWordCount));
    }

    private List<String> sanitizeTags(List<String> tags) {
        if (tags == null) {
            return List.of();
        }

        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .limit(12)
                .toList();
    }

    private String trimToNull(String value) {
        return StringUtils.trimToNull(value);
    }

    private <T> T defaultIfNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
