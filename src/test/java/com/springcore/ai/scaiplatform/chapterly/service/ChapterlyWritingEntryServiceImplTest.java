package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingEntryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingEntry;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingEntryRepository;
import com.springcore.ai.scaiplatform.chapterly.service.impl.ChapterlyWritingEntryServiceImpl;
import com.springcore.ai.scaiplatform.core.entity.User;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChapterlyWritingEntryServiceImplTest {

    @Mock
    private ChapterlyWritingEntryRepository entryRepository;

    @Mock
    private ChapterlyOwnershipService ownershipService;

    @InjectMocks
    private ChapterlyWritingEntryServiceImpl service;

    @Test
    void createEntryUpdatesStoryAndChapterWordTotals() {
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
                .currentWordCount(50)
                .targetWordCount(500)
                .build();
        chapter.setOwner(owner);
        when(ownershipService.requireUser(7L)).thenReturn(owner);
        when(ownershipService.requireChapter(11L, 22L, 7L)).thenReturn(chapter);
        when(entryRepository.save(any(ChapterlyWritingEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createEntry(7L, CreateChapterlyWritingEntryRequest.builder()
                .storyId(11L)
                .chapterId(22L)
                .entryDate(LocalDate.of(2026, 6, 21))
                .wordsWritten(200)
                .minutesSpent(30)
                .notesAdded(1)
                .build());

        assertThat(story.getCurrentWordCount()).isEqualTo(300);
        assertThat(story.getProgressPercent()).isEqualTo(30);
        assertThat(chapter.getCurrentWordCount()).isEqualTo(250);
        assertThat(chapter.getProgressPercent()).isEqualTo(50);
    }

    @Test
    void createEntryCanSkipManuscriptWordTotals() {
        User owner = User.builder().id(7L).email("writer@example.com").build();
        ChapterlyStory story = story(owner, 11L, 100);
        ChapterlyChapter chapter = chapter(owner, story, 22L, 50);
        when(ownershipService.requireUser(7L)).thenReturn(owner);
        when(ownershipService.requireChapter(11L, 22L, 7L)).thenReturn(chapter);
        when(entryRepository.save(any(ChapterlyWritingEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChapterlyWritingEntryResponse response = service.createEntry(7L, CreateChapterlyWritingEntryRequest.builder()
                .storyId(11L)
                .chapterId(22L)
                .entryDate(LocalDate.of(2026, 6, 21))
                .wordsWritten(200)
                .minutesSpent(30)
                .applyToManuscriptTotals(false)
                .build());

        assertThat(story.getCurrentWordCount()).isEqualTo(100);
        assertThat(chapter.getCurrentWordCount()).isEqualTo(50);
        assertThat(response.getApplyToManuscriptTotals()).isFalse();
    }

    @Test
    void updateEntryMovesWordDeltaBetweenTargets() {
        User owner = User.builder().id(7L).email("writer@example.com").build();
        ChapterlyStory oldStory = story(owner, 11L, 300);
        ChapterlyChapter oldChapter = chapter(owner, oldStory, 22L, 250);
        ChapterlyStory newStory = story(owner, 12L, 100);
        ChapterlyChapter newChapter = chapter(owner, newStory, 23L, 40);
        ChapterlyWritingEntry entry = ChapterlyWritingEntry.builder()
                .id(99L)
                .story(oldStory)
                .chapter(oldChapter)
                .entryDate(LocalDate.of(2026, 6, 20))
                .wordsWritten(200)
                .minutesSpent(30)
                .notesAdded(1)
                .build();
        entry.setOwner(owner);
        when(entryRepository.findByIdAndOwnerId(99L, 7L)).thenReturn(Optional.of(entry));
        when(ownershipService.requireChapter(12L, 23L, 7L)).thenReturn(newChapter);
        when(entryRepository.save(any(ChapterlyWritingEntry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateEntry(7L, 99L, UpdateChapterlyWritingEntryRequest.builder()
                .storyId(12L)
                .chapterId(23L)
                .wordsWritten(50)
                .build());

        assertThat(oldStory.getCurrentWordCount()).isEqualTo(100);
        assertThat(oldChapter.getCurrentWordCount()).isEqualTo(50);
        assertThat(newStory.getCurrentWordCount()).isEqualTo(150);
        assertThat(newChapter.getCurrentWordCount()).isEqualTo(90);
        assertThat(entry.getStory()).isSameAs(newStory);
        assertThat(entry.getChapter()).isSameAs(newChapter);
    }

    @Test
    void deleteEntryRequiresOwner() {
        when(entryRepository.findByIdAndOwnerId(99L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteEntry(7L, 99L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Writing entry");
    }

    private ChapterlyStory story(User owner, Long id, int currentWordCount) {
        ChapterlyStory story = ChapterlyStory.builder()
                .id(id)
                .title("Draft " + id)
                .currentWordCount(currentWordCount)
                .targetWordCount(1000)
                .build();
        story.setOwner(owner);
        return story;
    }

    private ChapterlyChapter chapter(User owner, ChapterlyStory story, Long id, int currentWordCount) {
        ChapterlyChapter chapter = ChapterlyChapter.builder()
                .id(id)
                .story(story)
                .title("Chapter " + id)
                .chapterNumber(1)
                .currentWordCount(currentWordCount)
                .targetWordCount(500)
                .build();
        chapter.setOwner(owner);
        return chapter;
    }
}
