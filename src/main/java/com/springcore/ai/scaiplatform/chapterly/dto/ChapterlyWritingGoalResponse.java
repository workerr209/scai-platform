package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalMetric;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalPeriod;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalScope;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingGoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyWritingGoalResponse {
    private Long id;
    private Long ownerUserId;
    private Long storyId;
    private Long chapterId;
    private ChapterlyGoalScope scope;
    private ChapterlyGoalPeriod period;
    private ChapterlyGoalMetric metric;
    private Integer targetValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static ChapterlyWritingGoalResponse from(ChapterlyWritingGoal goal) {
        return ChapterlyWritingGoalResponse.builder()
                .id(goal.getId())
                .ownerUserId(goal.getOwner().getId())
                .storyId(goal.getStory() == null ? null : goal.getStory().getId())
                .chapterId(goal.getChapter() == null ? null : goal.getChapter().getId())
                .scope(goal.getScope())
                .period(goal.getPeriod())
                .metric(goal.getMetric())
                .targetValue(goal.getTargetValue())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .active(goal.getActive())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}
