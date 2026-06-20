package com.springcore.ai.scaiplatform.chapterly.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CreateChapterlyChapterRequest {
    @NotBlank
    @Size(max = 180)
    private String title;

    @Positive
    private Integer chapterNumber;

    @PositiveOrZero
    private Integer targetWordCount;
}
