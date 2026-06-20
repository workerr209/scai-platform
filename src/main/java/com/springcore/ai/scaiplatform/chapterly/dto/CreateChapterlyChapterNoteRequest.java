package com.springcore.ai.scaiplatform.chapterly.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChapterlyChapterNoteRequest {
    @NotBlank
    private String body;
    private Boolean pinned;
}
