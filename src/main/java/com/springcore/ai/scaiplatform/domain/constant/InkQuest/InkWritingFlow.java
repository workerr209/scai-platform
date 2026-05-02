package com.springcore.ai.scaiplatform.domain.constant.InkQuest;

import lombok.Getter;

@Getter
public enum InkWritingFlow {
    FIRE("Fire", "danger"),
    OK("Ok", "info"),
    SLOW("Slow", "warn");

    private final String label;
    private final String severity;

    InkWritingFlow(String label, String severity) {
        this.label = label;
        this.severity = severity;
    }
}
