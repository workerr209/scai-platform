package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyChapterStatus;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyChapterResponse {
    private Long id;
    private Long storyId;
    private Long ownerUserId;
    private String title;
    private Integer chapterNumber;
    private ChapterlyChapterStatus status;
    private Integer targetWordCount;
    private Integer currentWordCount;
    private Integer progressPercent;
    private String body;
    private Instant createdAt;
    private Instant updatedAt;

    public static ChapterlyChapterResponse from(ChapterlyChapter chapter) {
        return ChapterlyChapterResponse.builder()
                .id(chapter.getId())
                .storyId(chapter.getStory().getId())
                .ownerUserId(chapter.getOwner().getId())
                .title(chapter.getTitle())
                .chapterNumber(chapter.getChapterNumber())
                .status(chapter.getStatus())
                .targetWordCount(chapter.getTargetWordCount())
                .currentWordCount(chapter.getCurrentWordCount())
                .progressPercent(chapter.getProgressPercent())
                .body(chapter.getBody())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }
}
