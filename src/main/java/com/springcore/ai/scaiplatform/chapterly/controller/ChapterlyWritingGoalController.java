package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingGoalResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingGoalRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingGoalRequest;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyWritingGoalService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chapterly/writing-goals")
@RequiredArgsConstructor
public class ChapterlyWritingGoalController {

    private final ChapterlyWritingGoalService goalService;

    @GetMapping
    public ResponseEntity<List<ChapterlyWritingGoalResponse>> listGoals(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean activeOnly
    ) {
        return ResponseEntity.ok(goalService.listGoals(principal.getId(), activeOnly));
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<ChapterlyWritingGoalResponse> getGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long goalId
    ) {
        return ResponseEntity.ok(goalService.getGoal(principal.getId(), goalId));
    }

    @PostMapping
    public ResponseEntity<ChapterlyWritingGoalResponse> createGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CreateChapterlyWritingGoalRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goalService.createGoal(principal.getId(), request));
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<ChapterlyWritingGoalResponse> updateGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long goalId,
            @RequestBody @Valid UpdateChapterlyWritingGoalRequest request
    ) {
        return ResponseEntity.ok(goalService.updateGoal(principal.getId(), goalId, request));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long goalId
    ) {
        goalService.deleteGoal(principal.getId(), goalId);
        return ResponseEntity.noContent().build();
    }
}
