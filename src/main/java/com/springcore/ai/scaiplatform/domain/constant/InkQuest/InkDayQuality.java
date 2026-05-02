package com.springcore.ai.scaiplatform.domain.constant.InkQuest;

import lombok.Getter;

@Getter
public enum InkDayQuality {
    GOOD("Good", "success"),
    FAIR("Fair", "warn"),
    POOR("Poor", "danger"),
    NONE("None", "secondary");

    private final String label;
    private final String severity;

    InkDayQuality(String label, String severity) {
        this.label = label;
        this.severity = severity;
    }
}
