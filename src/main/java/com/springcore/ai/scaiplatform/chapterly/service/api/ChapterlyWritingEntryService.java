package com.springcore.ai.scaiplatform.chapterly.service.api;

import com.springcore.ai.scaiplatform.chapterly.dto.ChapterlyWritingEntryResponse;
import com.springcore.ai.scaiplatform.chapterly.dto.CreateChapterlyWritingEntryRequest;
import com.springcore.ai.scaiplatform.chapterly.dto.UpdateChapterlyWritingEntryRequest;

import java.time.LocalDate;
import java.util.List;

public interface ChapterlyWritingEntryService {
    List<ChapterlyWritingEntryResponse> listEntries(Long ownerUserId, LocalDate startDate, LocalDate endDate);

    ChapterlyWritingEntryResponse getEntry(Long ownerUserId, Long entryId);

    ChapterlyWritingEntryResponse createEntry(Long ownerUserId, CreateChapterlyWritingEntryRequest request);

    ChapterlyWritingEntryResponse updateEntry(Long ownerUserId, Long entryId, UpdateChapterlyWritingEntryRequest request);

    void deleteEntry(Long ownerUserId, Long entryId);
}
