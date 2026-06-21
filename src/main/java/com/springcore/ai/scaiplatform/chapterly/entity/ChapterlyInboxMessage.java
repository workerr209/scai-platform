package com.springcore.ai.scaiplatform.chapterly.entity;

import com.springcore.ai.scaiplatform.core.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "chapterly_inbox_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterlyInboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyInboxMessage_GENERATOR")
    @SequenceGenerator(name = "ChapterlyInboxMessage_GENERATOR", sequenceName = "ChapterlyInboxMessage_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "conversation_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_chapterly_inbox_message_conversation")
    )
    private ChapterlyInboxConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "sender_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_chapterly_inbox_message_sender")
    )
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "recipient_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_chapterly_inbox_message_recipient")
    )
    private User recipient;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private Instant readAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }
}
