package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapterNote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyChapterNoteResponse {
    private Long id;
    private Long storyId;
    private Long chapterId;
    private Long ownerUserId;
    private String body;
    private Boolean pinned;
    private Instant createdAt;
    private Instant updatedAt;

    public static ChapterlyChapterNoteResponse from(ChapterlyChapterNote note) {
        return ChapterlyChapterNoteResponse.builder()
                .id(note.getId())
                .storyId(note.getStory().getId())
                .chapterId(note.getChapter().getId())
                .ownerUserId(note.getOwner().getId())
                .body(note.getBody())
                .pinned(note.getPinned())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
