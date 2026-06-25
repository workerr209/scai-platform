package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyAudienceRating;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryStatus;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryVisibility;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyStoryResponse {
    private Long id;
    private Long ownerUserId;
    private String title;
    private String penName;
    private String genre;
    private List<String> tags;
    private ChapterlyLanguage language;
    private ChapterlyAudienceRating audienceRating;
    private String summary;
    private String coverFileId;
    private ChapterlyStoryStatus status;
    private ChapterlyStoryVisibility visibility;
    private Integer targetWordCount;
    private Integer dailyWordTarget;
    private Integer currentWordCount;
    private Integer progressPercent;
    private String privateNote;
    private Instant createdAt;
    private Instant updatedAt;

    public static ChapterlyStoryResponse from(ChapterlyStory story) {
        int wordCount = story.getCurrentWordCount() == null ? 0 : story.getCurrentWordCount();
        return ChapterlyStoryResponse.builder()
                .id(story.getId())
                .ownerUserId(story.getOwner().getId())
                .title(story.getTitle())
                .penName(story.getPenName())
                .genre(story.getGenre())
                .tags(story.getTags() == null ? List.of() : List.copyOf(story.getTags()))
                .language(story.getLanguage())
                .audienceRating(story.getAudienceRating())
                .summary(story.getSummary())
                .coverFileId(story.getCoverFileId())
                .status(story.getStatus())
                .visibility(story.getVisibility())
                .targetWordCount(story.getTargetWordCount())
                .dailyWordTarget(story.getDailyWordTarget())
                .currentWordCount(wordCount)
                .progressPercent(calculateProgressPercent(wordCount, story.getTargetWordCount()))
                .privateNote(story.getPrivateNote())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }

    private static int calculateProgressPercent(int currentWordCount, Integer targetWordCount) {
        if (targetWordCount == null || targetWordCount <= 0) return 0;
        return Math.min(100, (int) Math.round((currentWordCount * 100.0) / targetWordCount));
    }
}
