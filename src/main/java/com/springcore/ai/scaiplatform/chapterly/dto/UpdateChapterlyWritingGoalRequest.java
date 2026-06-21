package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalMetric;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalPeriod;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalScope;
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
public class UpdateChapterlyWritingGoalRequest {
    private Long storyId;
    private Long chapterId;
    private ChapterlyGoalScope scope;
    private ChapterlyGoalPeriod period;
    private ChapterlyGoalMetric metric;

    @Positive
    private Integer targetValue;

    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}
