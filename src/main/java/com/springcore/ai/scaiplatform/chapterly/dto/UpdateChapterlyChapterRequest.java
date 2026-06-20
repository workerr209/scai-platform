package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyChapterStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChapterlyChapterRequest {
    @Size(max = 180)
    private String title;

    @Positive
    private Integer chapterNumber;

    private ChapterlyChapterStatus status;

    @PositiveOrZero
    private Integer targetWordCount;

    @PositiveOrZero
    private Integer currentWordCount;
}
