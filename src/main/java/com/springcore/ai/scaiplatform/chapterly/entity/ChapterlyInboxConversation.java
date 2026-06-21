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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "chapterly_inbox_conversation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chapterly_inbox_participants",
                        columnNames = {"participant_one_user_id", "participant_two_user_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterlyInboxConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyInboxConversation_GENERATOR")
    @SequenceGenerator(name = "ChapterlyInboxConversation_GENERATOR", sequenceName = "ChapterlyInboxConversation_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "participant_one_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_chapterly_inbox_participant_one")
    )
    private User participantOne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "participant_two_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_chapterly_inbox_participant_two")
    )
    private User participantTwo;

    @Column(columnDefinition = "TEXT")
    private String lastMessagePreview;

    private Long lastMessageSenderUserId;

    private Instant lastMessageAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
