package com.springcore.ai.scaiplatform.chapterly.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyEvent {
    private String eventName;
    private Long actorUserId;
    private Long targetUserId;
    private String aggregateType;
    private Long aggregateId;
    private Map<String, Object> payload;
    private Instant occurredAt;
}
