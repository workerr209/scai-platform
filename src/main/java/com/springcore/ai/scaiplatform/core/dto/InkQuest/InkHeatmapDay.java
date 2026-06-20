package com.springcore.ai.scaiplatform.core.dto.InkQuest;

import com.springcore.ai.scaiplatform.core.domain.constant.InkQuest.InkDayQuality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InkHeatmapDay {
    private String date;
    private InkDayQuality quality;
}
