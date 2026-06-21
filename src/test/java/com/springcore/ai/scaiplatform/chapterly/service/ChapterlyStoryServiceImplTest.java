package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyStoryRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.messaging.ChapterlyEventPublisher;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterNoteRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyStoryRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingEntryRepository;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingGoalRepository;
import com.springcore.ai.scaiplatform.core.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChapterlyStoryServiceImplTest {

    @Mock
    private ChapterlyStoryRepository storyRepository;

    @Mock
    private ChapterlyChapterRepository chapterRepository;

    @Mock
    private ChapterlyChapterNoteRepository noteRepository;

    @Mock
    private ChapterlyWritingEntryRepository entryRepository;

    @Mock
    private ChapterlyWritingGoalRepository goalRepository;

    @Mock
    private ChapterlyOwnershipService ownershipService;

    @Mock
    private ChapterlyEventPublisher eventPublisher;

    @InjectMocks
    private ChapterlyStoryServiceImpl service;

    @Test
    void updateStoryCanClearNullableFormFields() {
        User owner = User.builder().id(7L).email("writer@example.com").build();
        ChapterlyStory story = ChapterlyStory.builder()
                .id(11L)
                .title("Draft")
                .penName("Mira Drafts")
                .genre("Fantasy")
                .summary("Summary")
                .privateNote("Private note")
                .targetWordCount(50_000)
                .dailyWordTarget(1_000)
                .currentWordCount(125)
                .progressPercent(1)
                .build();
        story.setOwner(owner);

        when(ownershipService.requireStory(11L, 7L)).thenReturn(story);
        when(storyRepository.save(any(ChapterlyStory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateStory(7L, 11L, UpdateChapterlyStoryRequest.builder()
                .penName(null)
                .genre(null)
                .summary(null)
                .privateNote(null)
                .targetWordCount(null)
                .dailyWordTarget(null)
                .build());

        assertThat(story.getPenName()).isNull();
        assertThat(story.getGenre()).isNull();
        assertThat(story.getSummary()).isNull();
        assertThat(story.getPrivateNote()).isNull();
        assertThat(story.getTargetWordCount()).isNull();
        assertThat(story.getDailyWordTarget()).isNull();
        assertThat(story.getProgressPercent()).isZero();
    }
}
