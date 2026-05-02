package com.springcore.ai.scaiplatform.domain.constant.InkQuest;

import lombok.Getter;

@Getter
public enum InkChapterStatus {
    PENDING("Pending", "secondary"),
    WRITING("Writing", "info"),
    FINISHED("Finished", "success");

    private final String label;
    private final String severity;

    InkChapterStatus(String label, String severity) {
        this.label = label;
        this.severity = severity;
    }
}
