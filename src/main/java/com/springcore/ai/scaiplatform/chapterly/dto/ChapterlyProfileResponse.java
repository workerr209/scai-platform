package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyMode;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyRole;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterlyProfileResponse {
    private Long id;
    private Long userId;
    private String displayName;
    private String avatarFileId;
    private String avatarUrl;
    private ChapterlyLanguage preferredLanguage;
    private Set<ChapterlyRole> roles;
    private ChapterlyMode defaultMode;
    private ChapterlyMode lastActiveMode;
    private Boolean onboardingCompleted;

    public static ChapterlyProfileResponse from(ChapterlyProfile profile) {
        return ChapterlyProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .displayName(profile.getDisplayName())
                .avatarFileId(profile.getAvatarFileId())
                .avatarUrl(profile.getAvatarUrl())
                .preferredLanguage(profile.getPreferredLanguage())
                .roles(profile.getRoles())
                .defaultMode(profile.getDefaultMode())
                .lastActiveMode(profile.getLastActiveMode())
                .onboardingCompleted(profile.getOnboardingCompleted())
                .build();
    }
}
