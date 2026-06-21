package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyWritingEntryResponse {
    private Long id;
    private Long ownerUserId;
    private Long storyId;
    private Long chapterId;
    private LocalDate entryDate;
    private Integer wordsWritten;
    private Integer minutesSpent;
    private Integer notesAdded;
    private Boolean applyToManuscriptTotals;
    private Instant createdAt;
    private Instant updatedAt;

    public static ChapterlyWritingEntryResponse from(ChapterlyWritingEntry entry) {
        return ChapterlyWritingEntryResponse.builder()
                .id(entry.getId())
                .ownerUserId(entry.getOwner().getId())
                .storyId(entry.getStory() == null ? null : entry.getStory().getId())
                .chapterId(entry.getChapter() == null ? null : entry.getChapter().getId())
                .entryDate(entry.getEntryDate())
                .wordsWritten(entry.getWordsWritten())
                .minutesSpent(entry.getMinutesSpent())
                .notesAdded(entry.getNotesAdded())
                .applyToManuscriptTotals(entry.getApplyToManuscriptTotals())
                .createdAt(entry.getCreatedAt())
                .updatedAt(entry.getUpdatedAt())
                .build();
    }
}
