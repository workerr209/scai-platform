package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyStoryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyStoryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyStoryRequest;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyStoryService;
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
@RequestMapping("/api/v1/chapterly/stories")
@RequiredArgsConstructor
public class ChapterlyStoryController {

    private final ChapterlyStoryService storyService;

    @GetMapping
    public ResponseEntity<List<ChapterlyStoryResponse>> listStories(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(storyService.listStories(principal.getId()));
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<ChapterlyStoryResponse> getStory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId
    ) {
        return ResponseEntity.ok(storyService.getStory(principal.getId(), storyId));
    }

    @PostMapping
    public ResponseEntity<ChapterlyStoryResponse> createStory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CreateChapterlyStoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storyService.createStory(principal.getId(), request));
    }

    @PutMapping("/{storyId}")
    public ResponseEntity<ChapterlyStoryResponse> updateStory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId,
            @RequestBody @Valid UpdateChapterlyStoryRequest request
    ) {
        return ResponseEntity.ok(storyService.updateStory(principal.getId(), storyId, request));
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<Void> deleteStory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long storyId
    ) {
        storyService.deleteStory(principal.getId(), storyId);
        return ResponseEntity.noContent().build();
    }
}
