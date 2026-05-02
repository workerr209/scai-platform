package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.InkQuest.InkDashboardSummary;
import com.springcore.ai.scaiplatform.dto.InkQuest.InkEntrySearchReq;
import com.springcore.ai.scaiplatform.entity.InkQuest.*;

import java.util.Date;
import java.util.List;

public interface InkQuestService {
    InkDashboardSummary getDashboard(Long emId);

    List<InkProject> searchProjects(Long emId);
    InkProject getProject(Long id, Long emId);
    InkProject saveProject(InkProject form);
    boolean deleteProject(Long id, Long emId);

    List<InkChapter> searchChapters(Long projectId, Long emId);
    InkChapter getChapter(Long id, Long emId);
    InkChapter saveChapter(InkChapter form);
    boolean deleteChapter(Long id, Long emId);

    List<InkDailyEntry> searchEntries(InkEntrySearchReq criteria);
    InkDailyEntry getEntryByDate(Date entryDate, Long emId);
    InkDailyEntry saveEntry(InkDailyEntry form);

    InkWritingGoal getGoals(Long emId);
    InkWritingGoal saveGoals(InkWritingGoal form);

    InkSettings getSettings(Long emId);
    InkSettings saveSettings(InkSettings form);

    List<InkNote> searchNotes(Long emId);
    InkNote getNote(Long id, Long emId);
    InkNote saveNote(InkNote form);
    boolean deleteNote(Long id, Long emId);
}
