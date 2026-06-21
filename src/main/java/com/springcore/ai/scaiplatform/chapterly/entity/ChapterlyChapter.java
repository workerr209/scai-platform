package com.springcore.ai.scaiplatform.chapterly.entity;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyChapterStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "chapterly_chapter",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chapterly_chapter_story_number", columnNames = {"story_id", "chapter_number"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChapterlyChapter extends ChapterlyOwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyChapter_GENERATOR")
    @SequenceGenerator(name = "ChapterlyChapter_GENERATOR", sequenceName = "ChapterlyChapter_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false, foreignKey = @ForeignKey(name = "fk_chapterly_chapter_story"))
    private ChapterlyStory story;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyChapterStatus status;

    private Integer targetWordCount;
    private Integer currentWordCount;
    private Integer progressPercent;

    @Column(columnDefinition = "LONGTEXT")
    private String body;

    @PrePersist
    void chapterPrePersist() {
        if (status == null) {
            status = ChapterlyChapterStatus.PLANNED;
        }
        if (currentWordCount == null) {
            currentWordCount = 0;
        }
        if (progressPercent == null) {
            progressPercent = 0;
        }
    }
}
