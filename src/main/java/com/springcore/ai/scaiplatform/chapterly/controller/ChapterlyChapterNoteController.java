package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyChapterNoteResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyChapterNoteRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterNoteRequest;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyChapterNoteService;
import com.springcore.ai.scaiplatform.core.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chapterly/stories/{storyId}/chapters/{chapterId}/notes")
@RequiredArgsConstructor
public class ChapterlyChapterNoteController {

    private final ChapterlyChapterNoteService noteService;

    @GetMapping
    public ResponseEntity<List<ChapterlyChapterNoteResponse>> listNotes(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(noteService.listNotes(principal.getId(), storyId, chapterId));
    }

    @PostMapping
    public ResponseEntity<ChapterlyChapterNoteResponse> createNote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId,
            @RequestBody @Valid CreateChapterlyChapterNoteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(principal.getId(), storyId, chapterId, request));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<ChapterlyChapterNoteResponse> updateNote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId,
            @PathVariable Long noteId,
            @RequestBody @Valid UpdateChapterlyChapterNoteRequest request
    ) {
        return ResponseEntity.ok(noteService.updateNote(principal.getId(), storyId, chapterId, noteId, request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId,
            @PathVariable Long noteId
    ) {
        noteService.deleteNote(principal.getId(), storyId, chapterId, noteId);
        return ResponseEntity.noContent().build();
    }
}
