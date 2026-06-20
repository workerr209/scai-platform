package com.springcore.ai.scaiplatform.core.dto.InkQuest;

import com.springcore.ai.scaiplatform.core.entity.InkQuest.InkChapter;
import com.springcore.ai.scaiplatform.core.entity.InkQuest.InkProject;
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
    private Integer todayScore;
    private Integer wordsToday;
    private Integer wordsGoal;
    private Integer focusToday;
    private Integer focusGoal;
    private Integer streakDays;
    private Integer consistencyGoal;
    private List<InkWeeklyPoint> weekly;
    private List<InkCumulativePoint> cumulative;
    private List<InkHeatmapDay> heatmap;
    private InkProject currentProject;
    private InkChapter currentChapter;
}
