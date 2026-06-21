package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxConversation;
import com.springcore.ai.scaiplatform.core.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyInboxConversationResponse {
    private Long conversationId;
    private Long otherUserId;
    private String otherDisplayName;
    private String lastMessagePreview;
    private Instant lastMessageAt;
    private Boolean lastMessageFromMe;
    private Long unreadCount;

    public static ChapterlyInboxConversationResponse from(
            ChapterlyInboxConversation conversation,
            Long currentUserId,
            String otherDisplayName,
            long unreadCount
    ) {
        User otherUser = conversation.getParticipantOne().getId().equals(currentUserId)
                ? conversation.getParticipantTwo()
                : conversation.getParticipantOne();

        return ChapterlyInboxConversationResponse.builder()
                .conversationId(conversation.getId())
                .otherUserId(otherUser.getId())
                .otherDisplayName(otherDisplayName)
                .lastMessagePreview(conversation.getLastMessagePreview())
                .lastMessageAt(conversation.getLastMessageAt())
                .lastMessageFromMe(conversation.getLastMessageSenderUserId() != null
                        && conversation.getLastMessageSenderUserId().equals(currentUserId))
                .unreadCount(unreadCount)
                .build();
    }
}
