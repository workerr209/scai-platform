package com.springcore.ai.scaiplatform.chapterly.service.api;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxConversationResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxMessageResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxPreviewResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.SendChapterlyInboxMessageRequest;

import java.util.List;

public interface ChapterlyInboxService {
    ChapterlyInboxPreviewResponse getPreview(Long userId, int limit);

    List<ChapterlyInboxConversationResponse> listConversations(Long userId);

    List<ChapterlyInboxMessageResponse> listMessages(Long userId, Long conversationId);

    ChapterlyInboxMessageResponse sendMessage(Long senderUserId, SendChapterlyInboxMessageRequest request);

    void markConversationRead(Long userId, Long conversationId);
}
