package com.springcore.ai.scaiplatform.dto.InkQuest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InkCumulativePoint {
    private String month;
    private Integer words;
}
