package com.springcore.ai.scaiplatform.chapterly.controller;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyProfileResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CompleteOnboardingRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyProfileRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateLastActiveModeRequest;
import com.springcore.ai.scaiplatform.chapterly.service.ChapterlyProfileService;
import com.springcore.ai.scaiplatform.core.dto.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chapterly/profile/me")
@RequiredArgsConstructor
public class ChapterlyProfileController {

    private final ChapterlyProfileService profileService;

    @GetMapping
    public ResponseEntity<ChapterlyProfileResponse> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(profileService.getOrCreateProfile(principal.getId()));
    }

    @PutMapping
    public ResponseEntity<ChapterlyProfileResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UpdateChapterlyProfileRequest request
    ) {
        return ResponseEntity.ok(profileService.updateProfile(principal.getId(), request));
    }

    @PutMapping("/onboarding")
    public ResponseEntity<ChapterlyProfileResponse> completeOnboarding(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid CompleteOnboardingRequest request
    ) {
        return ResponseEntity.ok(profileService.completeOnboarding(principal.getId(), request));
    }

    @PutMapping("/last-active-mode")
    public ResponseEntity<ChapterlyProfileResponse> updateLastActiveMode(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UpdateLastActiveModeRequest request
    ) {
        return ResponseEntity.ok(profileService.updateLastActiveMode(principal.getId(), request));
    }
}
