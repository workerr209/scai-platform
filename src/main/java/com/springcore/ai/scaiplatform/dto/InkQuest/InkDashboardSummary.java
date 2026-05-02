package com.springcore.ai.scaiplatform.dto.InkQuest;

import com.springcore.ai.scaiplatform.entity.InkQuest.InkChapter;
import com.springcore.ai.scaiplatform.entity.InkQuest.InkProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InkDashboardSummary {
    private Long todayScore;
    private Long wordsToday;
    private Long wordsGoal;
    private Long focusToday;
    private Long focusGoal;
    private Long streakDays;
    private Long consistencyGoal;
    private List<InkWeeklyPoint> weekly;
    private List<InkCumulativePoint> cumulative;
    private List<InkHeatmapDay> heatmap;
    private InkProject currentProject;
    private InkChapter currentChapter;
}
