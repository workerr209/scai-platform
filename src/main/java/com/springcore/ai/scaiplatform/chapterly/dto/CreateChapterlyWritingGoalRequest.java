package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalMetric;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalPeriod;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalScope;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterlyWritingGoalRequest {
    private Long storyId;
    private Long chapterId;

    @NotNull
    private ChapterlyGoalScope scope;

    @NotNull
    private ChapterlyGoalPeriod period;

    @NotNull
    private ChapterlyGoalMetric metric;

    @NotNull
    @Positive
    private Integer targetValue;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;
}
