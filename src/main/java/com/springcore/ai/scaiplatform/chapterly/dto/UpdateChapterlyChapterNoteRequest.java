package com.springcore.ai.scaiplatform.chapterly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChapterlyChapterNoteRequest {
    private String body;
    private Boolean pinned;
}
