package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyAudienceRating;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryStatus;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterlyStoryRequest {
    @NotBlank
    @Size(max = 180)
    private String title;

    @Size(max = 120)
    private String penName;

    @Size(max = 80)
    private String genre;

    private List<@Size(max = 60) String> tags;

    private ChapterlyLanguage language;

    private ChapterlyAudienceRating audienceRating;

    private String summary;

    @Size(max = 120)
    private String coverFileId;

    private String coverUrl;

    private ChapterlyStoryStatus status;

    private ChapterlyStoryVisibility visibility;

    @PositiveOrZero
    private Integer targetWordCount;

    @PositiveOrZero
    private Integer dailyWordTarget;

    private String privateNote;
}
