package com.springcore.ai.scaiplatform.chapterly.service.impl;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalScope;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingGoalResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingGoalRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingGoalRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingGoal;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingGoalRepository;
import com.springcore.ai.scaiplatform.chapterly.service.ChapterlyOwnershipService;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyWritingGoalService;
import com.springcore.ai.scaiplatform.core.entity.User;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterlyWritingGoalServiceImpl implements ChapterlyWritingGoalService {

    private final ChapterlyWritingGoalRepository goalRepository;
    private final ChapterlyOwnershipService ownershipService;

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyWritingGoalResponse> listGoals(Long ownerUserId, Boolean activeOnly) {
        List<ChapterlyWritingGoal> goals = Boolean.TRUE.equals(activeOnly)
                ? goalRepository.findByOwnerIdAndActiveTrueOrderByStartDateDesc(ownerUserId)
                : goalRepository.findByOwnerIdOrderByStartDateDesc(ownerUserId);

        return goals.stream()
                .map(ChapterlyWritingGoalResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterlyWritingGoalResponse getGoal(Long ownerUserId, Long goalId) {
        return ChapterlyWritingGoalResponse.from(requireGoal(ownerUserId, goalId));
    }

    @Override
    @Transactional
    public ChapterlyWritingGoalResponse createGoal(Long ownerUserId, CreateChapterlyWritingGoalRequest request) {
        User owner = ownershipService.requireUser(ownerUserId);
        GoalTargets targets = resolveTargets(
                ownerUserId,
                request.getScope(),
                request.getStoryId(),
                request.getChapterId()
        );
        validateDateRange(request.getStartDate(), request.getEndDate());

        ChapterlyWritingGoal goal = ChapterlyWritingGoal.builder()
                .story(targets.story())
                .chapter(targets.chapter())
                .scope(request.getScope())
                .period(request.getPeriod())
                .metric(request.getMetric())
                .targetValue(request.getTargetValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() == null || request.getActive())
                .build();
        goal.setOwner(owner);

        return ChapterlyWritingGoalResponse.from(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public ChapterlyWritingGoalResponse updateGoal(Long ownerUserId, Long goalId, UpdateChapterlyWritingGoalRequest request) {
        ChapterlyWritingGoal goal = requireGoal(ownerUserId, goalId);

        ChapterlyGoalScope nextScope = request.getScope() == null ? goal.getScope() : request.getScope();
        Long nextStoryId = request.getStoryId() == null
                ? (goal.getStory() == null ? null : goal.getStory().getId())
                : request.getStoryId();
        Long nextChapterId = request.getChapterId() == null
                ? (goal.getChapter() == null ? null : goal.getChapter().getId())
                : request.getChapterId();
        GoalTargets targets = resolveTargets(ownerUserId, nextScope, nextStoryId, nextChapterId);

        goal.setScope(nextScope);
        goal.setStory(targets.story());
        goal.setChapter(targets.chapter());

        if (request.getPeriod() != null) {
            goal.setPeriod(request.getPeriod());
        }
        if (request.getMetric() != null) {
            goal.setMetric(request.getMetric());
        }
        if (request.getTargetValue() != null) {
            goal.setTargetValue(request.getTargetValue());
        }
        if (request.getStartDate() != null) {
            goal.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            goal.setEndDate(request.getEndDate());
        }
        if (request.getActive() != null) {
            goal.setActive(request.getActive());
        }

        validateDateRange(goal.getStartDate(), goal.getEndDate());

        return ChapterlyWritingGoalResponse.from(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public void deleteGoal(Long ownerUserId, Long goalId) {
        goalRepository.delete(requireGoal(ownerUserId, goalId));
    }

    private ChapterlyWritingGoal requireGoal(Long ownerUserId, Long goalId) {
        return goalRepository.findByIdAndOwnerId(goalId, ownerUserId)
                .orElseThrow(() -> new ValidationException("Writing goal was not found for this user"));
    }

    private GoalTargets resolveTargets(Long ownerUserId, ChapterlyGoalScope scope, Long storyId, Long chapterId) {
        if (scope == ChapterlyGoalScope.ACCOUNT) {
            if (storyId != null || chapterId != null) {
                throw new ValidationException("Account goals cannot target a story or chapter");
            }
            return new GoalTargets(null, null);
        }

        if (scope == ChapterlyGoalScope.STORY) {
            if (storyId == null || chapterId != null) {
                throw new ValidationException("Story goals require storyId and cannot target a chapter");
            }
            return new GoalTargets(ownershipService.requireStory(storyId, ownerUserId), null);
        }

        if (storyId == null || chapterId == null) {
            throw new ValidationException("Chapter goals require storyId and chapterId");
        }

        ChapterlyChapter chapter = ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        return new GoalTargets(chapter.getStory(), chapter);
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException("Goal endDate cannot be before startDate");
        }
    }

    private record GoalTargets(ChapterlyStory story, ChapterlyChapter chapter) {
    }
}
