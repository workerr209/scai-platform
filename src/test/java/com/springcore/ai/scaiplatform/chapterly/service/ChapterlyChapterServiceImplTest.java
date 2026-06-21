package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.messaging.ChapterlyEventPublisher;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterRepository;
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
class ChapterlyChapterServiceImplTest {

    @Mock
    private ChapterlyChapterRepository chapterRepository;

    @Mock
    private ChapterlyOwnershipService ownershipService;

    @Mock
    private ChapterlyEventPublisher eventPublisher;

    @InjectMocks
    private ChapterlyChapterServiceImpl service;

    @Test
    void updateChapterWordCountAdjustsParentStoryByDelta() {
        User owner = User.builder().id(7L).email("writer@example.com").build();
        ChapterlyStory story = ChapterlyStory.builder()
                .id(11L)
                .title("Draft")
                .currentWordCount(100)
                .targetWordCount(1000)
                .build();
        story.setOwner(owner);
        ChapterlyChapter chapter = ChapterlyChapter.builder()
                .id(22L)
                .story(story)
                .title("Opening")
                .chapterNumber(1)
                .currentWordCount(40)
                .targetWordCount(200)
                .build();
        chapter.setOwner(owner);
        when(ownershipService.requireChapter(11L, 22L, 7L)).thenReturn(chapter);
        when(chapterRepository.save(any(ChapterlyChapter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateChapter(7L, 11L, 22L, UpdateChapterlyChapterRequest.builder()
                .currentWordCount(70)
                .body("<p>More words now</p>")
                .build());

        assertThat(chapter.getCurrentWordCount()).isEqualTo(70);
        assertThat(chapter.getProgressPercent()).isEqualTo(35);
        assertThat(story.getCurrentWordCount()).isEqualTo(130);
        assertThat(story.getProgressPercent()).isEqualTo(13);
    }
}
