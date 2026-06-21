package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalMetric;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalPeriod;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalScope;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingGoalRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingGoal;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingGoalRepository;
import com.springcore.ai.scaiplatform.core.entity.User;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChapterlyWritingGoalServiceImplTest {

    @Mock
    private ChapterlyWritingGoalRepository goalRepository;

    @Mock
    private ChapterlyOwnershipService ownershipService;

    @InjectMocks
    private ChapterlyWritingGoalServiceImpl service;

    @Test
    void createStoryGoalLinksOwnedStory() {
        User owner = User.builder().id(7L).email("writer@example.com").build();
        ChapterlyStory story = ChapterlyStory.builder().id(11L).title("Draft").build();
        story.setOwner(owner);
        when(ownershipService.requireUser(7L)).thenReturn(owner);
        when(ownershipService.requireStory(11L, 7L)).thenReturn(story);
        when(goalRepository.save(any(ChapterlyWritingGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createGoal(7L, CreateChapterlyWritingGoalRequest.builder()
                .storyId(11L)
                .scope(ChapterlyGoalScope.STORY)
                .period(ChapterlyGoalPeriod.DAILY)
                .metric(ChapterlyGoalMetric.WORDS)
                .targetValue(1200)
                .startDate(LocalDate.of(2026, 6, 21))
                .build());

        ArgumentCaptor<ChapterlyWritingGoal> captor = ArgumentCaptor.forClass(ChapterlyWritingGoal.class);
        verify(goalRepository).save(captor.capture());
        ChapterlyWritingGoal saved = captor.getValue();
        assertThat(saved.getOwner()).isSameAs(owner);
        assertThat(saved.getStory()).isSameAs(story);
        assertThat(saved.getChapter()).isNull();
        assertThat(saved.getActive()).isTrue();
    }

    @Test
    void createAccountGoalRejectsStoryTarget() {
        when(ownershipService.requireUser(7L)).thenReturn(User.builder().id(7L).email("writer@example.com").build());

        CreateChapterlyWritingGoalRequest request = CreateChapterlyWritingGoalRequest.builder()
                .storyId(11L)
                .scope(ChapterlyGoalScope.ACCOUNT)
                .period(ChapterlyGoalPeriod.DAILY)
                .metric(ChapterlyGoalMetric.WORDS)
                .targetValue(1200)
                .startDate(LocalDate.of(2026, 6, 21))
                .build();

        assertThatThrownBy(() -> service.createGoal(7L, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Account goals");
        verify(goalRepository, never()).save(any());
    }

    @Test
    void getGoalRequiresOwner() {
        when(goalRepository.findByIdAndOwnerId(99L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getGoal(7L, 99L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Writing goal");
    }
}
