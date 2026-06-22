package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxConversationResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxMessageResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyInboxPreviewResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.SendChapterlyInboxMessageRequest;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyInboxService;
import com.springcore.ai.scaiplatform.core.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chapterly/inbox")
@RequiredArgsConstructor
public class ChapterlyInboxController {

    private final ChapterlyInboxService inboxService;

    @GetMapping("/preview")
    public ResponseEntity<ChapterlyInboxPreviewResponse> getPreview(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(inboxService.getPreview(principal.getId(), limit));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ChapterlyInboxConversationResponse>> listConversations(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(inboxService.listConversations(principal.getId()));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChapterlyInboxMessageResponse>> listMessages(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long conversationId
    ) {
        return ResponseEntity.ok(inboxService.listMessages(principal.getId(), conversationId));
    }

    @PostMapping("/messages")
    public ResponseEntity<ChapterlyInboxMessageResponse> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid SendChapterlyInboxMessageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inboxService.sendMessage(principal.getId(), request));
    }

    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markConversationRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long conversationId
    ) {
        inboxService.markConversationRead(principal.getId(), conversationId);
        return ResponseEntity.noContent().build();
    }
}
