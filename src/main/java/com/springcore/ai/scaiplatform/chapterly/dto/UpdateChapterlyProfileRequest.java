package com.springcore.ai.scaiplatform.chapterly.dto;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyLanguage;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyMode;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyRole;
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
public class UpdateChapterlyProfileRequest {
    @Size(max = 120)
    private String displayName;

    @Size(max = 120)
    private String avatarFileId;

    private String avatarUrl;

    private ChapterlyLanguage preferredLanguage;
    private Set<ChapterlyRole> roles;
    private ChapterlyMode defaultMode;
}
