package com.springcore.ai.scaiplatform.chapterly.service.api;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingGoalResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingGoalRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingGoalRequest;

import java.util.List;

public interface ChapterlyWritingGoalService {
    List<ChapterlyWritingGoalResponse> listGoals(Long ownerUserId, Boolean activeOnly);

    ChapterlyWritingGoalResponse getGoal(Long ownerUserId, Long goalId);

    ChapterlyWritingGoalResponse createGoal(Long ownerUserId, CreateChapterlyWritingGoalRequest request);

    ChapterlyWritingGoalResponse updateGoal(Long ownerUserId, Long goalId, UpdateChapterlyWritingGoalRequest request);

    void deleteGoal(Long ownerUserId, Long goalId);
}
