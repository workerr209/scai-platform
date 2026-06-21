package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterlyInboxMessageRepository extends JpaRepository<ChapterlyInboxMessage, Long> {
    List<ChapterlyInboxMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    long countByConversationIdAndRecipientIdAndReadAtIsNull(Long conversationId, Long recipientId);

    List<ChapterlyInboxMessage> findByConversationIdAndRecipientIdAndReadAtIsNull(Long conversationId, Long recipientId);
}
