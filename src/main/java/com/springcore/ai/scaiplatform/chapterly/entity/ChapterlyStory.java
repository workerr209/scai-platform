package com.springcore.ai.scaiplatform.chapterly.entity;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyAudienceRating;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryStatus;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyStoryVisibility;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapterly_story")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChapterlyStory extends ChapterlyOwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyStory_GENERATOR")
    @SequenceGenerator(name = "ChapterlyStory_GENERATOR", sequenceName = "ChapterlyStory_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(length = 120)
    private String penName;

    @Column(length = 80)
    private String genre;

    @ElementCollection
    @CollectionTable(name = "chapterly_story_tags", joinColumns = @jakarta.persistence.JoinColumn(name = "story_id"))
    @Column(name = "tag", length = 60)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChapterlyLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyAudienceRating audienceRating;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(length = 120)
    private String coverFileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyStoryStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyStoryVisibility visibility;

    private Integer targetWordCount;
    private Integer dailyWordTarget;

    @Formula("(SELECT COALESCE(SUM(c.currentWordCount), 0) FROM chapterly_chapter c WHERE c.story_id = id AND c.owner_user_id = owner_user_id)")
    private Integer currentWordCount;

    private Integer progressPercent;

    @Column(columnDefinition = "TEXT")
    private String privateNote;

    @PrePersist
    void storyPrePersist() {
        if (status == null) {
            status = ChapterlyStoryStatus.DRAFT;
        }
        if (language == null) {
            language = ChapterlyLanguage.EN;
        }
        if (audienceRating == null) {
            audienceRating = ChapterlyAudienceRating.TEEN;
        }
        if (visibility == null) {
            visibility = ChapterlyStoryVisibility.PRIVATE;
        }
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (progressPercent == null) {
            progressPercent = 0;
        }
    }
}
