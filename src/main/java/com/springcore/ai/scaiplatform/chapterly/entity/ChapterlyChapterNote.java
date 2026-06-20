package com.springcore.ai.scaiplatform.chapterly.entity;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chapterly_chapter_note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChapterlyChapterNote extends ChapterlyOwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyChapterNote_GENERATOR")
    @SequenceGenerator(name = "ChapterlyChapterNote_GENERATOR", sequenceName = "ChapterlyChapterNote_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false, foreignKey = @ForeignKey(name = "fk_chapterly_note_story"))
    private ChapterlyStory story;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false, foreignKey = @ForeignKey(name = "fk_chapterly_note_chapter"))
    private ChapterlyChapter chapter;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    private Boolean pinned;

    @PrePersist
    void notePrePersist() {
        if (pinned == null) {
            pinned = false;
        }
    }
}
