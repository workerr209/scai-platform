package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingEntryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyWritingEntryService;
import com.springcore.ai.scaiplatform.core.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chapterly/writing-entries")
@RequiredArgsConstructor
public class ChapterlyWritingEntryController {

    private final ChapterlyWritingEntryService entryService;

    @GetMapping
    public ResponseEntity<List<ChapterlyWritingEntryResponse>> listEntries(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(entryService.listEntries(principal.getId(), startDate, endDate));
    }

    @GetMapping("/{entryId}")
    public ResponseEntity<ChapterlyWritingEntryResponse> getEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long entryId
    ) {
        return ResponseEntity.ok(entryService.getEntry(principal.getId(), entryId));
    }

    @PostMapping
    public ResponseEntity<ChapterlyWritingEntryResponse> createEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CreateChapterlyWritingEntryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entryService.createEntry(principal.getId(), request));
    }

    @PutMapping("/{entryId}")
    public ResponseEntity<ChapterlyWritingEntryResponse> updateEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long entryId,
            @RequestBody @Valid UpdateChapterlyWritingEntryRequest request
    ) {
        return ResponseEntity.ok(entryService.updateEntry(principal.getId(), entryId, request));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long entryId
    ) {
        entryService.deleteEntry(principal.getId(), entryId);
        return ResponseEntity.noContent().build();
    }
}
