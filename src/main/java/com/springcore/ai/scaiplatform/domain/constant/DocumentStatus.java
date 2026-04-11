package com.springcore.ai.scaiplatform.domain.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DocumentStatus {
    DRAFT(0),
    WAITING(1),
    APPROVED(2),
    NOT_APPROVED(11),
    CANCEL(12);

    @JsonValue
    private final int value;

    DocumentStatus(int value) {
        this.value = value;
    }

    public String getLabel() {
        return switch (this) {
            case DRAFT -> "Drafts";
            case WAITING -> "Waiting";
            case APPROVED -> "Approved";
            case NOT_APPROVED -> "Not Approved";
            case CANCEL -> "Cancel";
        };
    }

    public String getSeverity() {
        return switch (this) {
            case DRAFT -> "secondary";
            case WAITING -> "warn";
            case APPROVED -> "success";
            case NOT_APPROVED -> "danger";
            case CANCEL -> "contrast";
        };
    }

}