package com.springcore.ai.scaiplatform.chapterly.service.impl;

import com.springcore.ai.scaiplatform.chapterly.config.ChapterlyMessagingConfig;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxConversationResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxMessageResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxPreviewResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.SendChapterlyInboxMessageRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxConversation;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxMessage;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyProfile;
import com.springcore.ai.scaiplatform.chapterly.messaging.ChapterlyEventPublisher;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyInboxConversationRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyInboxMessageRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyProfileRepository;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyInboxService;
import com.springcore.ai.scaiplatform.core.entity.User;
import com.springcore.ai.scaiplatform.core.repository.api.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChapterlyInboxServiceImpl implements ChapterlyInboxService {

    private static final int MAX_PREVIEW_LIMIT = 10;

    private final ChapterlyInboxConversationRepository conversationRepository;
    private final ChapterlyInboxMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChapterlyProfileRepository profileRepository;
    private final ChapterlyEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public ChapterlyInboxPreviewResponse getPreview(Long userId, int limit) {
        int resolvedLimit = Math.max(1, Math.min(limit, MAX_PREVIEW_LIMIT));
        List<ChapterlyInboxConversationResponse> conversations = listConversations(userId).stream()
                .limit(resolvedLimit)
                .toList();

        return ChapterlyInboxPreviewResponse.builder()
                .unreadCount(messageRepository.countByRecipientIdAndReadAtIsNull(userId))
                .conversations(conversations)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyInboxConversationResponse> listConversations(Long userId) {
        requireUser(userId);
        return conversationRepository.findByParticipantOneIdOrParticipantTwoIdOrderByLastMessageAtDesc(userId, userId)
                .stream()
                .map(conversation -> toConversationResponse(conversation, userId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyInboxMessageResponse> listMessages(Long userId, Long conversationId) {
        requireConversationForUser(conversationId, userId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(ChapterlyInboxMessageResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ChapterlyInboxMessageResponse sendMessage(Long senderUserId, SendChapterlyInboxMessageRequest request) {
        if (senderUserId.equals(request.getRecipientUserId())) {
            throw new ValidationException("Cannot send an inbox message to yourself");
        }

        User sender = requireUser(senderUserId);
        User recipient = requireUser(request.getRecipientUserId());
        ChapterlyInboxConversation conversation = findOrCreateConversation(sender, recipient);
        String body = request.getBody().trim();

        ChapterlyInboxMessage message = ChapterlyInboxMessage.builder()
                .conversation(conversation)
                .sender(sender)
                .recipient(recipient)
                .body(body)
                .build();

        ChapterlyInboxMessage saved = messageRepository.save(message);
        conversation.setLastMessagePreview(preview(body));
        conversation.setLastMessageSenderUserId(senderUserId);
        conversation.setLastMessageAt(saved.getCreatedAt() == null ? Instant.now() : saved.getCreatedAt());
        conversationRepository.save(conversation);

        eventPublisher.publishAfterCommit(
                ChapterlyMessagingConfig.INBOX_MESSAGE_CREATED,
                senderUserId,
                recipient.getId(),
                "inboxMessage",
                saved.getId(),
                Map.of("conversationId", conversation.getId())
        );

        return ChapterlyInboxMessageResponse.from(saved);
    }

    @Override
    @Transactional
    public void markConversationRead(Long userId, Long conversationId) {
        ChapterlyInboxConversation conversation = requireConversationForUser(conversationId, userId);
        List<ChapterlyInboxMessage> unreadMessages = messageRepository.findByConversationIdAndRecipientIdAndReadAtIsNull(
                conversationId,
                userId
        );

        if (unreadMessages.isEmpty()) {
            return;
        }

        Instant readAt = Instant.now();
        unreadMessages.forEach(message -> message.setReadAt(readAt));
        messageRepository.saveAll(unreadMessages);

        eventPublisher.publishAfterCommit(
                ChapterlyMessagingConfig.INBOX_MESSAGE_READ,
                userId,
                otherParticipant(conversation, userId).getId(),
                "inboxConversation",
                conversationId,
                Map.of("messageCount", unreadMessages.size())
        );
    }

    private ChapterlyInboxConversation findOrCreateConversation(User sender, User recipient) {
        ParticipantPair pair = canonicalPair(sender, recipient);
        return conversationRepository.findByParticipantOneIdAndParticipantTwoId(pair.one().getId(), pair.two().getId())
                .orElseGet(() -> conversationRepository.save(ChapterlyInboxConversation.builder()
                        .participantOne(pair.one())
                        .participantTwo(pair.two())
                        .build()));
    }

    private ChapterlyInboxConversation requireConversationForUser(Long conversationId, Long userId) {
        return conversationRepository.findByIdAndParticipantOneIdOrIdAndParticipantTwoId(
                        conversationId,
                        userId,
                        conversationId,
                        userId
                )
                .orElseThrow(() -> new ValidationException("Inbox conversation was not found for this user"));
    }

    private ChapterlyInboxConversationResponse toConversationResponse(ChapterlyInboxConversation conversation, Long userId) {
        User otherUser = otherParticipant(conversation, userId);
        long unreadCount = messageRepository.countByConversationIdAndRecipientIdAndReadAtIsNull(conversation.getId(), userId);
        return ChapterlyInboxConversationResponse.from(
                conversation,
                userId,
                displayNameFor(otherUser),
                unreadCount
        );
    }

    private User otherParticipant(ChapterlyInboxConversation conversation, Long userId) {
        if (conversation.getParticipantOne().getId().equals(userId)) {
            return conversation.getParticipantTwo();
        }
        if (conversation.getParticipantTwo().getId().equals(userId)) {
            return conversation.getParticipantOne();
        }
        throw new ValidationException("Inbox conversation was not found for this user");
    }

    private User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User was not found"));
    }

    private String displayNameFor(User user) {
        return profileRepository.findByUserId(user.getId())
                .map(ChapterlyProfile::getDisplayName)
                .filter(name -> !name.isBlank())
                .orElseGet(() -> {
                    if (user.getName() != null && !user.getName().isBlank()) {
                        return user.getName();
                    }
                    return user.getEmail();
                });
    }

    private String preview(String body) {
        if (body.length() <= 160) {
            return body;
        }
        return body.substring(0, 157) + "...";
    }

    private ParticipantPair canonicalPair(User first, User second) {
        return first.getId() < second.getId()
                ? new ParticipantPair(first, second)
                : new ParticipantPair(second, first);
    }

    private record ParticipantPair(User one, User two) {
    }
}
