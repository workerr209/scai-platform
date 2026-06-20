package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyChapterResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyChapterRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterRequest;
import com.springcore.ai.scaiplatform.chapterly.service.ChapterlyChapterService;
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
@RequestMapping("/api/v1/chapterly/stories/{storyId}/chapters")
@RequiredArgsConstructor
public class ChapterlyChapterController {

    private final ChapterlyChapterService chapterService;

    @GetMapping
    public ResponseEntity<List<ChapterlyChapterResponse>> listChapters(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId
    ) {
        return ResponseEntity.ok(chapterService.listChapters(principal.getId(), storyId));
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<ChapterlyChapterResponse> getChapter(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId
    ) {
        return ResponseEntity.ok(chapterService.getChapter(principal.getId(), storyId, chapterId));
    }

    @PostMapping
    public ResponseEntity<ChapterlyChapterResponse> createChapter(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @RequestBody @Valid CreateChapterlyChapterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chapterService.createChapter(principal.getId(), storyId, request));
    }

    @PutMapping("/{chapterId}")
    public ResponseEntity<ChapterlyChapterResponse> updateChapter(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId,
            @RequestBody @Valid UpdateChapterlyChapterRequest request
    ) {
        return ResponseEntity.ok(chapterService.updateChapter(principal.getId(), storyId, chapterId, request));
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> deleteChapter(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @PathVariable Long chapterId
    ) {
        chapterService.deleteChapter(principal.getId(), storyId, chapterId);
        return ResponseEntity.noContent().build();
    }
}
