package com.springcore.ai.scaiplatform.chapterly.service.impl;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyMode;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyRole;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyProfileResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CompleteOnboardingRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyProfileRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateLastActiveModeRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyProfile;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyProfileRepository;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyProfileService;
import com.springcore.ai.scaiplatform.core.entity.User;
import com.springcore.ai.scaiplatform.core.repository.api.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChapterlyProfileServiceImpl implements ChapterlyProfileService {

    private final ChapterlyProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ChapterlyProfileResponse getOrCreateProfile(Long userId) {
        return ChapterlyProfileResponse.from(getOrCreateEntity(userId));
    }

    @Override
    @Transactional
    public ChapterlyProfileResponse updateProfile(Long userId, UpdateChapterlyProfileRequest request) {
        ChapterlyProfile profile = getOrCreateEntity(userId);

        if (StringUtils.isNotBlank(request.getDisplayName())) {
            profile.setDisplayName(request.getDisplayName().trim());
        }

        if (request.getAvatarFileId() != null) {
            profile.setAvatarFileId(StringUtils.trimToNull(request.getAvatarFileId()));
        }

        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(StringUtils.trimToNull(request.getAvatarUrl()));
        }

        if (request.getPreferredLanguage() != null) {
            profile.setPreferredLanguage(request.getPreferredLanguage());
        }

        if (request.getRoles() != null) {
            validateRoles(request.getRoles());
            profile.setRoles(new LinkedHashSet<>(request.getRoles()));
        }

        if (request.getDefaultMode() != null) {
            validateDefaultMode(profile.getRoles(), request.getDefaultMode());
            profile.setDefaultMode(request.getDefaultMode());
        }

        return ChapterlyProfileResponse.from(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public ChapterlyProfileResponse completeOnboarding(Long userId, CompleteOnboardingRequest request) {
        validateRoles(request.getRoles());
        validateDefaultMode(request.getRoles(), request.getDefaultMode());

        ChapterlyProfile profile = getOrCreateEntity(userId);
        profile.setDisplayName(request.getDisplayName().trim());
        profile.setPreferredLanguage(request.getPreferredLanguage());
        profile.setRoles(new LinkedHashSet<>(request.getRoles()));
        profile.setDefaultMode(request.getDefaultMode());
        profile.setLastActiveMode(request.getDefaultMode());
        profile.setOnboardingCompleted(true);

        return ChapterlyProfileResponse.from(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public ChapterlyProfileResponse updateLastActiveMode(Long userId, UpdateLastActiveModeRequest request) {
        ChapterlyProfile profile = getOrCreateEntity(userId);
        validateDefaultMode(profile.getRoles(), request.getMode());
        profile.setLastActiveMode(request.getMode());

        return ChapterlyProfileResponse.from(profileRepository.save(profile));
    }

    private ChapterlyProfile getOrCreateEntity(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
    }

    private ChapterlyProfile createDefaultProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Authenticated user was not found"));

        ChapterlyProfile profile = ChapterlyProfile.builder()
                .user(user)
                .displayName(resolveDefaultDisplayName(user))
                .preferredLanguage(ChapterlyLanguage.EN)
                .roles(new LinkedHashSet<>())
                .onboardingCompleted(false)
                .build();

        return profileRepository.save(profile);
    }

    private String resolveDefaultDisplayName(User user) {
        if (StringUtils.isNotBlank(user.getName())) {
            return user.getName().trim();
        }

        if (StringUtils.isNotBlank(user.getEmail()) && user.getEmail().contains("@")) {
            return StringUtils.substringBefore(user.getEmail(), "@");
        }

        return "Chapterly user";
    }

    private void validateRoles(Set<ChapterlyRole> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new ValidationException("At least one Chapterly role is required");
        }
    }

    private void validateDefaultMode(Set<ChapterlyRole> roles, ChapterlyMode defaultMode) {
        if (defaultMode == ChapterlyMode.WRITE && !roles.contains(ChapterlyRole.WRITER)) {
            throw new ValidationException("WRITE mode requires WRITER role");
        }

        if (defaultMode == ChapterlyMode.READ && !roles.contains(ChapterlyRole.READER)) {
            throw new ValidationException("READ mode requires READER role");
        }
    }
}
