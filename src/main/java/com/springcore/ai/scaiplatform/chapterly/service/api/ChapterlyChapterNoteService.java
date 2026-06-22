package com.springcore.ai.scaiplatform.chapterly.service.api;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyChapterNoteResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyChapterNoteRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterNoteRequest;

import java.util.List;

public interface ChapterlyChapterNoteService {
    List<ChapterlyChapterNoteResponse> listNotes(Long ownerUserId, Long storyId, Long chapterId);

    ChapterlyChapterNoteResponse createNote(Long ownerUserId, Long storyId, Long chapterId, CreateChapterlyChapterNoteRequest request);

    ChapterlyChapterNoteResponse updateNote(Long ownerUserId, Long storyId, Long chapterId, Long noteId, UpdateChapterlyChapterNoteRequest request);

    void deleteNote(Long ownerUserId, Long storyId, Long chapterId, Long noteId);
}
