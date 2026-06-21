package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyInboxMessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderUserId;
    private Long recipientUserId;
    private String body;
    private Instant readAt;
    private Instant createdAt;

    public static ChapterlyInboxMessageResponse from(ChapterlyInboxMessage message) {
        return ChapterlyInboxMessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderUserId(message.getSender().getId())
                .recipientUserId(message.getRecipient().getId())
                .body(message.getBody())
                .readAt(message.getReadAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
