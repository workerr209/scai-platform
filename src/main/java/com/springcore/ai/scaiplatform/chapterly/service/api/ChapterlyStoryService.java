package com.springcore.ai.scaiplatform.chapterly.service.api;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyStoryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyStoryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyStoryRequest;

import java.util.List;

public interface ChapterlyStoryService {
    List<ChapterlyStoryResponse> listStories(Long ownerUserId);

    ChapterlyStoryResponse getStory(Long ownerUserId, Long storyId);

    ChapterlyStoryResponse createStory(Long ownerUserId, CreateChapterlyStoryRequest request);

    ChapterlyStoryResponse updateStory(Long ownerUserId, Long storyId, UpdateChapterlyStoryRequest request);

    void deleteStory(Long ownerUserId, Long storyId);
}
