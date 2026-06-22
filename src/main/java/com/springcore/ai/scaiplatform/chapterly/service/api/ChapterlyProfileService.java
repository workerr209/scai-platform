package com.springcore.ai.scaiplatform.chapterly.service.api;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyProfileResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CompleteOnboardingRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyProfileRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateLastActiveModeRequest;

public interface ChapterlyProfileService {
    ChapterlyProfileResponse getOrCreateProfile(Long userId);

    ChapterlyProfileResponse updateProfile(Long userId, UpdateChapterlyProfileRequest request);

    ChapterlyProfileResponse completeOnboarding(Long userId, CompleteOnboardingRequest request);

    ChapterlyProfileResponse updateLastActiveMode(Long userId, UpdateLastActiveModeRequest request);
}
