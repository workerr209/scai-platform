package com.springcore.ai.scaiplatform.chapterly.service;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingEntryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyStory;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyWritingEntry;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyWritingEntryRepository;
import com.springcore.ai.scaiplatform.core.entity.User;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterlyWritingEntryServiceImpl implements ChapterlyWritingEntryService {

    private final ChapterlyWritingEntryRepository entryRepository;
    private final ChapterlyOwnershipService ownershipService;

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyWritingEntryResponse> listEntries(Long ownerUserId, LocalDate startDate, LocalDate endDate) {
        List<ChapterlyWritingEntry> entries;
        if (startDate != null || endDate != null) {
            LocalDate from = startDate == null ? LocalDate.MIN : startDate;
            LocalDate to = endDate == null ? LocalDate.MAX : endDate;
            if (to.isBefore(from)) {
                throw new ValidationException("endDate cannot be before startDate");
            }
            entries = entryRepository.findByOwnerIdAndEntryDateBetweenOrderByEntryDateDesc(ownerUserId, from, to);
        } else {
            entries = entryRepository.findByOwnerIdOrderByEntryDateDesc(ownerUserId);
        }

        return entries.stream()
                .map(ChapterlyWritingEntryResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterlyWritingEntryResponse getEntry(Long ownerUserId, Long entryId) {
        return ChapterlyWritingEntryResponse.from(requireEntry(ownerUserId, entryId));
    }

    @Override
    @Transactional
    public ChapterlyWritingEntryResponse createEntry(Long ownerUserId, CreateChapterlyWritingEntryRequest request) {
        User owner = ownershipService.requireUser(ownerUserId);
        EntryTargets targets = resolveTargets(ownerUserId, request.getStoryId(), request.getChapterId());
        int wordsWritten = defaultZero(request.getWordsWritten());
        boolean applyToManuscriptTotals = defaultTrue(request.getApplyToManuscriptTotals());

        ChapterlyWritingEntry entry = ChapterlyWritingEntry.builder()
                .story(targets.story())
                .chapter(targets.chapter())
                .entryDate(request.getEntryDate())
                .wordsWritten(wordsWritten)
                .minutesSpent(defaultZero(request.getMinutesSpent()))
                .notesAdded(defaultZero(request.getNotesAdded()))
                .applyToManuscriptTotals(applyToManuscriptTotals)
                .build();
        entry.setOwner(owner);
        if (applyToManuscriptTotals) {
            applyWordDelta(targets.chapter(), wordsWritten);
        }

        return ChapterlyWritingEntryResponse.from(entryRepository.save(entry));
    }

    @Override
    @Transactional
    public ChapterlyWritingEntryResponse updateEntry(Long ownerUserId, Long entryId, UpdateChapterlyWritingEntryRequest request) {
        ChapterlyWritingEntry entry = requireEntry(ownerUserId, entryId);
        ChapterlyChapter previousChapter = entry.getChapter();
        int previousWords = defaultZero(entry.getWordsWritten());
        boolean previousApplyToManuscriptTotals = defaultTrue(entry.getApplyToManuscriptTotals());

        Long nextStoryId = request.getStoryId() == null
                ? (entry.getStory() == null ? null : entry.getStory().getId())
                : request.getStoryId();
        Long nextChapterId = request.getChapterId() == null
                ? (entry.getChapter() == null ? null : entry.getChapter().getId())
                : request.getChapterId();
        EntryTargets targets = resolveTargets(ownerUserId, nextStoryId, nextChapterId);
        int nextWords = request.getWordsWritten() == null ? previousWords : request.getWordsWritten();
        boolean nextApplyToManuscriptTotals = request.getApplyToManuscriptTotals() == null
                ? previousApplyToManuscriptTotals
                : request.getApplyToManuscriptTotals();

        if (previousApplyToManuscriptTotals) {
            applyWordDelta(previousChapter, -previousWords);
        }
        if (nextApplyToManuscriptTotals) {
            applyWordDelta(targets.chapter(), nextWords);
        }

        entry.setStory(targets.story());
        entry.setChapter(targets.chapter());
        entry.setApplyToManuscriptTotals(nextApplyToManuscriptTotals);
        if (request.getEntryDate() != null) {
            entry.setEntryDate(request.getEntryDate());
        }
        entry.setWordsWritten(nextWords);
        if (request.getMinutesSpent() != null) {
            entry.setMinutesSpent(request.getMinutesSpent());
        }
        if (request.getNotesAdded() != null) {
            entry.setNotesAdded(request.getNotesAdded());
        }

        return ChapterlyWritingEntryResponse.from(entryRepository.save(entry));
    }

    @Override
    @Transactional
    public void deleteEntry(Long ownerUserId, Long entryId) {
        ChapterlyWritingEntry entry = requireEntry(ownerUserId, entryId);
        if (defaultTrue(entry.getApplyToManuscriptTotals())) {
            applyWordDelta(entry.getChapter(), -defaultZero(entry.getWordsWritten()));
        }
        entryRepository.delete(entry);
    }

    private ChapterlyWritingEntry requireEntry(Long ownerUserId, Long entryId) {
        return entryRepository.findByIdAndOwnerId(entryId, ownerUserId)
                .orElseThrow(() -> new ValidationException("Writing entry was not found for this user"));
    }

    private EntryTargets resolveTargets(Long ownerUserId, Long storyId, Long chapterId) {
        if (chapterId != null && storyId == null) {
            throw new ValidationException("chapterId requires storyId");
        }

        if (chapterId != null) {
            ChapterlyChapter chapter = ownershipService.requireChapter(storyId, chapterId, ownerUserId);
            return new EntryTargets(chapter.getStory(), chapter);
        }

        if (storyId != null) {
            return new EntryTargets(ownershipService.requireStory(storyId, ownerUserId), null);
        }

        return new EntryTargets(null, null);
    }

    private void applyWordDelta(ChapterlyChapter chapter, int delta) {
        if (chapter != null) {
            int nextChapterWords = Math.max(0, defaultZero(chapter.getCurrentWordCount()) + delta);
            chapter.setCurrentWordCount(nextChapterWords);
            chapter.setProgressPercent(calculateProgressPercent(nextChapterWords, chapter.getTargetWordCount()));
        }
    }

    private int calculateProgressPercent(Integer currentWordCount, Integer targetWordCount) {
        if (currentWordCount == null || targetWordCount == null || targetWordCount <= 0) {
            return 0;
        }

        return Math.min(100, (int) Math.round((currentWordCount * 100.0) / targetWordCount));
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private boolean defaultTrue(Boolean value) {
        return value == null || value;
    }

    private record EntryTargets(ChapterlyStory story, ChapterlyChapter chapter) {
    }
}
