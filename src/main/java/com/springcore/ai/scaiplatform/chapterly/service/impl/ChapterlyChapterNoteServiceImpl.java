package com.springcore.ai.scaiplatform.chapterly.service.impl;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyChapterNoteResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyChapterNoteRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyChapterNoteRequest;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapter;
import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyChapterNote;
import com.springcore.ai.scaiplatform.chapterly.repository.ChapterlyChapterNoteRepository;
import com.springcore.ai.scaiplatform.chapterly.service.ChapterlyOwnershipService;
import com.springcore.ai.scaiplatform.chapterly.service.api.ChapterlyChapterNoteService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterlyChapterNoteServiceImpl implements ChapterlyChapterNoteService {

    private final ChapterlyChapterNoteRepository noteRepository;
    private final ChapterlyOwnershipService ownershipService;

    @Override
    @Transactional(readOnly = true)
    public List<ChapterlyChapterNoteResponse> listNotes(Long ownerUserId, Long storyId, Long chapterId) {
        ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        return noteRepository.findByChapterIdAndOwnerIdOrderByPinnedDescUpdatedAtDesc(chapterId, ownerUserId)
                .stream()
                .map(ChapterlyChapterNoteResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ChapterlyChapterNoteResponse createNote(
            Long ownerUserId,
            Long storyId,
            Long chapterId,
            CreateChapterlyChapterNoteRequest request
    ) {
        ChapterlyChapter chapter = ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        ChapterlyChapterNote note = ChapterlyChapterNote.builder()
                .story(chapter.getStory())
                .chapter(chapter)
                .body(request.getBody().trim())
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .build();
        note.setOwner(chapter.getOwner());

        return ChapterlyChapterNoteResponse.from(noteRepository.save(note));
    }

    @Override
    @Transactional
    public ChapterlyChapterNoteResponse updateNote(
            Long ownerUserId,
            Long storyId,
            Long chapterId,
            Long noteId,
            UpdateChapterlyChapterNoteRequest request
    ) {
        ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        ChapterlyChapterNote note = noteRepository.findByIdAndOwnerId(noteId, ownerUserId)
                .filter(found -> found.getStory().getId().equals(storyId) && found.getChapter().getId().equals(chapterId))
                .orElseThrow(() -> new ValidationException("Chapter note was not found for this user"));

        if (StringUtils.isNotBlank(request.getBody())) {
            note.setBody(request.getBody().trim());
        }

        if (request.getPinned() != null) {
            note.setPinned(request.getPinned());
        }

        return ChapterlyChapterNoteResponse.from(noteRepository.save(note));
    }

    @Override
    @Transactional
    public void deleteNote(Long ownerUserId, Long storyId, Long chapterId, Long noteId) {
        ownershipService.requireChapter(storyId, chapterId, ownerUserId);
        ChapterlyChapterNote note = noteRepository.findByIdAndOwnerId(noteId, ownerUserId)
                .filter(found -> found.getStory().getId().equals(storyId) && found.getChapter().getId().equals(chapterId))
                .orElseThrow(() -> new ValidationException("Chapter note was not found for this user"));
        noteRepository.delete(note);
    }
}
