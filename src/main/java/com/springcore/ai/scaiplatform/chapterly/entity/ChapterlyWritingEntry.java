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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "chapterly_writing_entry",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chapterly_entry_owner_date_story_chapter", columnNames = {"owner_user_id", "entry_date", "story_id", "chapter_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChapterlyWritingEntry extends ChapterlyOwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyWritingEntry_GENERATOR")
    @SequenceGenerator(name = "ChapterlyWritingEntry_GENERATOR", sequenceName = "ChapterlyWritingEntry_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", foreignKey = @ForeignKey(name = "fk_chapterly_entry_story"))
    private ChapterlyStory story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", foreignKey = @ForeignKey(name = "fk_chapterly_entry_chapter"))
    private ChapterlyChapter chapter;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    private Integer wordsWritten;
    private Integer minutesSpent;
    private Integer notesAdded;
    private Boolean applyToManuscriptTotals;

    @PrePersist
    void entryPrePersist() {
        if (wordsWritten == null) {
            wordsWritten = 0;
        }
        if (minutesSpent == null) {
            minutesSpent = 0;
        }
        if (notesAdded == null) {
            notesAdded = 0;
        }
        if (applyToManuscriptTotals == null) {
            applyToManuscriptTotals = true;
        }
    }
}
