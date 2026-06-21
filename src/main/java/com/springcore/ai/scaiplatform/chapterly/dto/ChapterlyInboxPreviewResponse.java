package com.springcore.ai.scaiplatform.chapterly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyInboxPreviewResponse {
    private Long unreadCount;
    private List<ChapterlyInboxConversationResponse> conversations;
}
