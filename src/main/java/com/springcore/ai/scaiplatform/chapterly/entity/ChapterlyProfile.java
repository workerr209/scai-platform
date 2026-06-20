package com.springcore.ai.scaiplatform.chapterly.entity;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyMode;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyRole;
import com.springcore.ai.scaiplatform.core.entity.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "chapterly_profile",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chapterly_profile_user", columnNames = "user_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterlyProfile {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyProfile_GENERATOR")
    @SequenceGenerator(name = "ChapterlyProfile_GENERATOR", sequenceName = "ChapterlyProfile_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 120)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChapterlyLanguage preferredLanguage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "chapterly_profile_roles",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Set<ChapterlyRole> roles = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ChapterlyMode defaultMode;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private ChapterlyMode lastActiveMode;

    @Column(nullable = false)
    private Boolean onboardingCompleted;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.preferredLanguage == null) {
            this.preferredLanguage = ChapterlyLanguage.EN;
        }

        if (this.roles == null) {
            this.roles = new LinkedHashSet<>();
        }

        if (this.onboardingCompleted == null) {
            this.onboardingCompleted = false;
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
