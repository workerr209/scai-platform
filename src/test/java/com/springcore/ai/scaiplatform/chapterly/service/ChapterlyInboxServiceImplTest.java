package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxPreviewResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.SendChapterlyInboxMessageRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxConversation;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxMessage;
import com.springcore.ai.scaiplatform.chapterly.messaging.ChapterlyEventPublisher;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyInboxConversationRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyInboxMessageRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyProfileRepository;
import com.springcore.ai.scaiplatform.chapterly.service.impl.ChapterlyInboxServiceImpl;
import com.springcore.ai.scaiplatform.core.entity.User;
import com.springcore.ai.scaiplatform.core.repository.api.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChapterlyInboxServiceImplTest {

    @Mock
    private ChapterlyInboxConversationRepository conversationRepository;

    @Mock
    private ChapterlyInboxMessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChapterlyProfileRepository profileRepository;

    @Mock
    private ChapterlyEventPublisher eventPublisher;

    @InjectMocks
    private ChapterlyInboxServiceImpl service;

    @Test
    void sendMessageCreatesCanonicalConversationAndPublishesEvent() {
        User sender = user(7L, "writer@example.com", "Writer");
        User recipient = user(3L, "reader@example.com", "Reader");
        ChapterlyInboxConversation conversation = ChapterlyInboxConversation.builder()
                .id(11L)
                .participantOne(recipient)
                .participantTwo(sender)
                .build();

        when(userRepository.findById(7L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(3L)).thenReturn(Optional.of(recipient));
        when(conversationRepository.findByParticipantOneIdAndParticipantTwoId(3L, 7L)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(ChapterlyInboxConversation.class)))
                .thenReturn(conversation);
        when(messageRepository.save(any(ChapterlyInboxMessage.class))).thenAnswer(invocation -> {
            ChapterlyInboxMessage message = invocation.getArgument(0);
            message.setId(99L);
            message.setCreatedAt(Instant.parse("2026-06-21T01:02:03Z"));
            return message;
        });

        var response = service.sendMessage(7L, SendChapterlyInboxMessageRequest.builder()
                .recipientUserId(3L)
                .body("  Hello from the desk  ")
                .build());

        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getConversationId()).isEqualTo(11L);
        assertThat(conversation.getLastMessagePreview()).isEqualTo("Hello from the desk");
        assertThat(conversation.getLastMessageSenderUserId()).isEqualTo(7L);
        verify(eventPublisher).publishAfterCommit(
                eq("chapterly.inbox.message.created"),
                eq(7L),
                eq(3L),
                eq("inboxMessage"),
                eq(99L),
                anyMap()
        );
    }

    @Test
    void sendMessageRejectsSelfMessage() {
        assertThatThrownBy(() -> service.sendMessage(7L, SendChapterlyInboxMessageRequest.builder()
                .recipientUserId(7L)
                .body("Note to self")
                .build()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("yourself");
    }

    @Test
    void previewReturnsUnreadCountAndLimitedConversations() {
        User currentUser = user(7L, "writer@example.com", "Writer");
        User otherUser = user(8L, "reader@example.com", "Reader");
        ChapterlyInboxConversation first = conversation(1L, currentUser, otherUser, "First", 7L);
        ChapterlyInboxConversation second = conversation(2L, currentUser, otherUser, "Second", 8L);

        when(userRepository.findById(7L)).thenReturn(Optional.of(currentUser));
        when(conversationRepository.findByParticipantOneIdOrParticipantTwoIdOrderByLastMessageAtDesc(7L, 7L))
                .thenReturn(List.of(first, second));
        when(profileRepository.findByUserId(8L)).thenReturn(Optional.empty());
        when(messageRepository.countByRecipientIdAndReadAtIsNull(7L)).thenReturn(4L);
        when(messageRepository.countByConversationIdAndRecipientIdAndReadAtIsNull(any(), eq(7L))).thenReturn(1L);

        ChapterlyInboxPreviewResponse response = service.getPreview(7L, 1);

        assertThat(response.getUnreadCount()).isEqualTo(4L);
        assertThat(response.getConversations()).hasSize(1);
        assertThat(response.getConversations().get(0).getLastMessagePreview()).isEqualTo("First");
    }

    private User user(Long id, String email, String name) {
        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .build();
    }

    private ChapterlyInboxConversation conversation(Long id, User one, User two, String preview, Long senderUserId) {
        return ChapterlyInboxConversation.builder()
                .id(id)
                .participantOne(one)
                .participantTwo(two)
                .lastMessagePreview(preview)
                .lastMessageSenderUserId(senderUserId)
                .lastMessageAt(Instant.parse("2026-06-21T01:02:03Z"))
                .build();
    }
}
