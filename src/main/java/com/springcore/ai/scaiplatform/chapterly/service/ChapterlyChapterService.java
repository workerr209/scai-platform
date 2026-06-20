package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyChapterResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyChapterRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterRequest;

import java.util.List;

public interface ChapterlyChapterService {
    List<ChapterlyChapterResponse> listChapters(Long ownerUserId, Long storyId);

    ChapterlyChapterResponse getChapter(Long ownerUserId, Long storyId, Long chapterId);

    ChapterlyChapterResponse createChapter(Long ownerUserId, Long storyId, CreateChapterlyChapterRequest request);

    ChapterlyChapterResponse updateChapter(Long ownerUserId, Long storyId, Long chapterId, UpdateChapterlyChapterRequest request);

    void deleteChapter(Long ownerUserId, Long storyId, Long chapterId);
}
