package com.springcore.ai.scaiplatform.chapterly.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterlyWritingEntryRequest {
    private Long storyId;
    private Long chapterId;

    @NotNull
    private LocalDate entryDate;

    @PositiveOrZero
    private Integer wordsWritten;

    @PositiveOrZero
    private Integer minutesSpent;

    @PositiveOrZero
    private Integer notesAdded;

    private Boolean applyToManuscriptTotals;
}
