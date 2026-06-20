package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyMode;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOnboardingRequest {
    @NotBlank
    @Size(max = 120)
    private String displayName;

    @NotNull
    private ChapterlyLanguage preferredLanguage;

    @NotEmpty
    private Set<ChapterlyRole> roles;

    @NotNull
    private ChapterlyMode defaultMode;
}
