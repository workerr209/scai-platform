package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.domain.constant.InkQuest.InkChapterStatus;
import com.springcore.ai.scaiplatform.domain.constant.InkQuest.InkDayQuality;
import com.springcore.ai.scaiplatform.dto.InkQuest.*;
import com.springcore.ai.scaiplatform.entity.InkQuest.*;
import com.springcore.ai.scaiplatform.repository.api.*;
import com.springcore.ai.scaiplatform.security.UserContext;
import com.springcore.ai.scaiplatform.service.api.InkQuestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Slf4j
@Service
public class InkQuestServiceImpl implements InkQuestService {
    private final InkProjectRepository projectRepository;
    private final InkChapterRepository chapterRepository;
    private final InkDailyEntryRepository entryRepository;
    private final InkWritingGoalRepository goalRepository;
    private final InkSettingsRepository settingsRepository;
    private final InkNoteRepository noteRepository;

    @Autowired
    public InkQuestServiceImpl(
            InkProjectRepository projectRepository,
            InkChapterRepository chapterRepository,
            InkDailyEntryRepository entryRepository,
            InkWritingGoalRepository goalRepository,
            InkSettingsRepository settingsRepository,
            InkNoteRepository noteRepository
    ) {
        this.projectRepository = projectRepository;
        this.chapterRepository = chapterRepository;
        this.entryRepository = entryRepository;
        this.goalRepository = goalRepository;
        this.settingsRepository = settingsRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public InkDashboardSummary getDashboard(Long emId) {
        Long ownerId = owner(emId);
        InkWritingGoal goals = getGoals(ownerId);
        InkSettings settings = getSettings(ownerId);
        List<InkProject> projects = projectRepository.findByEmIdOrderByUpdatedAtDesc(ownerId);
        InkProject currentProject = resolveCurrentProject(ownerId, settings, projects);
        InkChapter currentChapter = currentProject == null ? null
                : chapterRepository.findFirstByEmIdAndProjectIdAndStatusOrderByChapterNoAsc(
                        ownerId,
                        currentProject.getId(),
                        InkChapterStatus.WRITING
                ).orElse(null);
        List<InkDailyEntry> todayEntries = entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(
                ownerId,
                dateFromLocalDate(LocalDate.now()),
                endOfDay(dateFromLocalDate(LocalDate.now()))
        );

        long wordsToday = todayEntries.stream().mapToLong(e -> value(e.getWords())).sum();
        long focusToday = todayEntries.stream().mapToLong(e -> value(e.getFocusMinutes())).sum();
        long wordsGoal = Math.max(1L, value(goals.getDailyWords()));
        long focusGoal = Math.max(1L, value(goals.getDailyFocus()));
        long todayScore = Math.min(100L, Math.round(((wordsToday / (double) wordsGoal) * 0.6 + (focusToday / (double) focusGoal) * 0.4) * 100));

        return InkDashboardSummary.builder()
                .todayScore(todayScore)
                .wordsToday(wordsToday)
                .wordsGoal(wordsGoal)
                .focusToday(focusToday)
                .focusGoal(focusGoal)
                .streakDays(currentStreak(ownerId))
                .consistencyGoal(value(goals.getStreakTarget()))
                .weekly(buildWeekly(ownerId, goals))
                .cumulative(buildCumulative(ownerId))
                .heatmap(buildHeatmap(ownerId, goals))
                .currentProject(currentProject)
                .currentChapter(currentChapter)
                .build();
    }

    @Override
    public List<InkProject> searchProjects(Long emId) {
        return projectRepository.findByEmIdOrderByUpdatedAtDesc(owner(emId));
    }

    @Override
    public InkProject getProject(Long id, Long emId) {
        return projectRepository.findByIdAndEmId(id, owner(emId))
                .orElseThrow(() -> new RuntimeException("Project Not Found"));
    }

    @Override
    @Transactional
    public InkProject saveProject(InkProject form) {
        Long ownerId = owner(form.getEmId());
        ensureSetup(ownerId);
        boolean creating = form.getId() == null;
        Long requestedChapters = Math.max(1L, defaultLong(form.getTotalChapters(), 20L));

        InkProject target = creating ? new InkProject() : getProject(form.getId(), ownerId);
        target.setEmId(ownerId);
        target.setTitle(form.getTitle());
        target.setCover(normalizeCover(form.getCover()));
        target.setSummary(form.getSummary());
        target.setUpdatedAt(new Date());
        if (creating) {
            target.setTotalChapters(requestedChapters);
            target.setFinishedChapters(0L);
            target.setProgressPercent(0L);
        } else {
            target.setTotalChapters(target.getTotalChapters());
            target.setFinishedChapters(target.getFinishedChapters());
            target.setProgressPercent(target.getProgressPercent());
        }
        InkProject saved = projectRepository.save(target);
        if (creating) {
            createInitialChapters(ownerId, saved.getId(), requestedChapters);
            syncProjectProgress(ownerId, saved.getId());
            settingsRepository.findByEmId(ownerId).ifPresent(settings -> {
                if (settings.getDefaultProjectId() == null) {
                    settings.setDefaultProjectId(saved.getId());
                    settingsRepository.save(settings);
                }
            });
            return getProject(saved.getId(), ownerId);
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean deleteProject(Long id, Long emId) {
        Long ownerId = owner(emId);
        if (!projectRepository.existsByIdAndEmId(id, ownerId)) return false;
        entryRepository.deleteByEmIdAndProjectId(ownerId, id);
        chapterRepository.deleteByEmIdAndProjectId(ownerId, id);
        projectRepository.deleteByIdAndEmId(id, ownerId);
        settingsRepository.findByEmId(ownerId).ifPresent(settings -> {
            if (Objects.equals(settings.getDefaultProjectId(), id)) {
                settings.setDefaultProjectId(null);
                settingsRepository.save(settings);
            }
        });
        return true;
    }

    @Override
    public List<InkChapter> searchChapters(Long projectId, Long emId) {
        return chapterRepository.findByEmIdAndProjectIdOrderByChapterNoAsc(owner(emId), projectId);
    }

    @Override
    public InkChapter getChapter(Long id, Long emId) {
        return chapterRepository.findByIdAndEmId(id, owner(emId))
                .orElseThrow(() -> new RuntimeException("Chapter Not Found"));
    }

    @Override
    @Transactional
    public InkChapter saveChapter(InkChapter form) {
        Long ownerId = owner(form.getEmId());
        InkChapter target = form.getId() != null ? getChapter(form.getId(), ownerId) : new InkChapter();
        target.setEmId(ownerId);
        target.setProjectId(form.getProjectId());
        target.setChapterNo(form.getChapterNo());
        target.setTitle(form.getTitle());
        target.setStatus(form.getStatus() != null ? form.getStatus() : InkChapterStatus.PENDING);
        target.setGoalWords(defaultLong(form.getGoalWords(), 1000L));
        target.setWrittenWords(defaultLong(form.getWrittenWords(), 0L));
        target.setNotes(form.getNotes());
        target.setUpdatedAt(new Date());
        InkChapter saved = chapterRepository.save(target);
        syncProjectProgress(ownerId, saved.getProjectId());
        return saved;
    }

    @Override
    @Transactional
    public boolean deleteChapter(Long id, Long emId) {
        Long ownerId = owner(emId);
        Optional<InkChapter> chapterOpt = chapterRepository.findByIdAndEmId(id, ownerId);
        if (chapterOpt.isEmpty()) return false;
        Long projectId = chapterOpt.get().getProjectId();
        entryRepository.deleteByEmIdAndChapterId(ownerId, id);
        chapterRepository.deleteByIdAndEmId(id, ownerId);
        syncProjectProgress(ownerId, projectId);
        return true;
    }

    @Override
    public List<InkDailyEntry> searchEntries(InkEntrySearchReq criteria) {
        Long ownerId = owner(criteria != null ? criteria.getEmId() : null);
        if (criteria != null && criteria.getProjectId() != null) {
            return entryRepository.findByEmIdAndProjectIdOrderByEntryDateDesc(ownerId, criteria.getProjectId());
        }
        if (criteria != null && criteria.getChapterId() != null) {
            return entryRepository.findByEmIdAndChapterIdOrderByEntryDateDesc(ownerId, criteria.getChapterId());
        }
        if (criteria != null && (criteria.getDateFrom() != null || criteria.getDateTo() != null)) {
            Date from = startOfDay(criteria.getDateFrom() != null ? criteria.getDateFrom() : dateFromLocalDate(LocalDate.of(1970, 1, 1)));
            Date to = endOfDay(criteria.getDateTo() != null ? criteria.getDateTo() : new Date());
            return entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(ownerId, from, to);
        }
        return entryRepository.findByEmIdOrderByEntryDateDesc(ownerId);
    }

    @Override
    public InkDailyEntry getEntryByDate(Date entryDate, Long emId) {
        Long ownerId = owner(emId);
        Date from = startOfDay(entryDate);
        Date to = endOfDay(entryDate);
        return entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(ownerId, from, to)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    @Transactional
    public InkDailyEntry saveEntry(InkDailyEntry form) {
        Long ownerId = owner(form.getEmId());
        Date entryDate = startOfDay(form.getEntryDate() != null ? form.getEntryDate() : new Date());
        InkDailyEntry previous = form.getId() != null
                ? entryRepository.findByIdAndEmId(form.getId(), ownerId).orElse(null)
                : null;
        InkDailyEntry previousSnapshot = copyEntry(previous);
        Long projectId = form.getProjectId();
        if (projectId == null && form.getChapterId() != null) {
            projectId = getChapter(form.getChapterId(), ownerId).getProjectId();
        }

        InkDailyEntry target = previous != null ? previous : new InkDailyEntry();
        target.setEmId(ownerId);
        target.setEntryDate(entryDate);
        target.setProjectId(projectId);
        target.setChapterId(form.getChapterId());
        target.setWords(defaultLong(form.getWords(), 0L));
        target.setFocusMinutes(defaultLong(form.getFocusMinutes(), 0L));
        target.setSessions(defaultLong(form.getSessions(), 1L));
        target.setFlow(form.getFlow());
        target.setNote(form.getNote());
        target.setQuality(form.getQuality() != null ? form.getQuality() : toQuality(target.getWords(), getGoals(ownerId).getDailyWords()));

        InkDailyEntry saved = entryRepository.save(target);
        applyEntryChapterDelta(ownerId, previousSnapshot, saved);
        return saved;
    }

    @Override
    public InkWritingGoal getGoals(Long emId) {
        Long ownerId = owner(emId);
        return goalRepository.findByEmId(ownerId).orElseGet(() -> defaultGoals(ownerId));
    }

    @Override
    @Transactional
    public InkWritingGoal saveGoals(InkWritingGoal form) {
        Long ownerId = owner(form.getEmId());
        InkWritingGoal target = goalRepository.findByEmId(ownerId).orElseGet(InkWritingGoal::new);
        target.setEmId(ownerId);
        target.setDailyWords(Math.max(1L, value(form.getDailyWords())));
        target.setMonthlyWords(Math.max(1L, value(form.getMonthlyWords())));
        target.setDailyFocus(Math.max(1L, value(form.getDailyFocus())));
        target.setStreakTarget(Math.max(1L, value(form.getStreakTarget())));
        return goalRepository.save(target);
    }

    @Override
    public InkSettings getSettings(Long emId) {
        Long ownerId = owner(emId);
        return settingsRepository.findByEmId(ownerId).orElseGet(() -> defaultSettings(ownerId));
    }

    @Override
    @Transactional
    public InkSettings saveSettings(InkSettings form) {
        Long ownerId = owner(form.getEmId());
        InkSettings target = settingsRepository.findByEmId(ownerId).orElseGet(InkSettings::new);
        target.setEmId(ownerId);
        target.setDefaultProjectId(form.getDefaultProjectId());
        target.setWordGoalReminder(Boolean.TRUE.equals(form.getWordGoalReminder()));
        target.setReminderTime(form.getReminderTime());
        target.setAutoLogStreak(Boolean.TRUE.equals(form.getAutoLogStreak()));
        target.setShowWordCountInMenu(Boolean.TRUE.equals(form.getShowWordCountInMenu()));
        return settingsRepository.save(target);
    }

    @Override
    public List<InkNote> searchNotes(Long emId) {
        return noteRepository.findByEmIdOrderByUpdatedAtDesc(owner(emId));
    }

    @Override
    public InkNote getNote(Long id, Long emId) {
        return noteRepository.findByIdAndEmId(id, owner(emId))
                .orElseThrow(() -> new RuntimeException("Note Not Found"));
    }

    @Override
    @Transactional
    public InkNote saveNote(InkNote form) {
        Long ownerId = owner(form.getEmId());
        Date now = new Date();
        InkNote target = form.getId() != null ? getNote(form.getId(), ownerId) : new InkNote();
        target.setEmId(ownerId);
        target.setTitle(form.getTitle());
        target.setContent(form.getContent());
        target.setTags(form.getTags());
        if (target.getCreatedAt() == null) target.setCreatedAt(now);
        target.setUpdatedAt(now);
        return noteRepository.save(target);
    }

    @Override
    @Transactional
    public boolean deleteNote(Long id, Long emId) {
        Long ownerId = owner(emId);
        if (noteRepository.findByIdAndEmId(id, ownerId).isEmpty()) return false;
        noteRepository.deleteByIdAndEmId(id, ownerId);
        return true;
    }

    private InkProject resolveCurrentProject(Long emId, InkSettings settings, List<InkProject> projects) {
        if (settings.getDefaultProjectId() != null) {
            Optional<InkProject> defaultProject = projectRepository.findByIdAndEmId(settings.getDefaultProjectId(), emId);
            if (defaultProject.isPresent()) return defaultProject.get();
        }
        return projects.isEmpty() ? null : projects.get(0);
    }

    private void ensureSetup(Long emId) {
        if (!goalRepository.existsByEmId(emId)) {
            goalRepository.save(defaultGoals(emId));
        }
        if (!settingsRepository.existsByEmId(emId)) {
            settingsRepository.save(defaultSettings(emId));
        }
    }

    private void createInitialChapters(Long emId, Long projectId, Long totalChapters) {
        for (long i = 1; i <= totalChapters; i++) {
            InkChapter chapter = InkChapter.builder()
                    .emId(emId)
                    .projectId(projectId)
                    .chapterNo(i)
                    .title("Chapter " + i)
                    .status(i == 1 ? InkChapterStatus.WRITING : InkChapterStatus.PENDING)
                    .goalWords(1000L)
                    .writtenWords(0L)
                    .updatedAt(new Date())
                    .build();
            chapterRepository.save(chapter);
        }
    }

    private void applyEntryChapterDelta(Long emId, InkDailyEntry previous, InkDailyEntry next) {
        Set<Long> touchedProjectIds = new HashSet<>();
        if (previous != null && previous.getChapterId() != null) {
            chapterRepository.findByIdAndEmId(previous.getChapterId(), emId).ifPresent(chapter -> {
                chapter.setWrittenWords(Math.max(0L, value(chapter.getWrittenWords()) - value(previous.getWords())));
                chapter.setUpdatedAt(new Date());
                chapterRepository.save(chapter);
                touchedProjectIds.add(chapter.getProjectId());
            });
        }
        if (next.getChapterId() != null) {
            chapterRepository.findByIdAndEmId(next.getChapterId(), emId).ifPresent(chapter -> {
                chapter.setWrittenWords(Math.min(value(chapter.getGoalWords()), value(chapter.getWrittenWords()) + value(next.getWords())));
                chapter.setUpdatedAt(new Date());
                chapterRepository.save(chapter);
                touchedProjectIds.add(chapter.getProjectId());
            });
        }
        touchedProjectIds.forEach(projectId -> syncProjectProgress(emId, projectId));
    }

    private InkDailyEntry copyEntry(InkDailyEntry source) {
        if (source == null) return null;
        return InkDailyEntry.builder()
                .id(source.getId())
                .emId(source.getEmId())
                .projectId(source.getProjectId())
                .chapterId(source.getChapterId())
                .entryDate(source.getEntryDate())
                .words(source.getWords())
                .focusMinutes(source.getFocusMinutes())
                .sessions(source.getSessions())
                .flow(source.getFlow())
                .quality(source.getQuality())
                .note(source.getNote())
                .build();
    }

    private void syncProjectProgress(Long emId, Long projectId) {
        if (projectId == null) return;
        projectRepository.findByIdAndEmId(projectId, emId).ifPresent(project -> {
            long total = chapterRepository.countByEmIdAndProjectId(emId, projectId);
            long finished = chapterRepository.countByEmIdAndProjectIdAndStatus(emId, projectId, InkChapterStatus.FINISHED);
            project.setTotalChapters(total);
            project.setFinishedChapters(finished);
            project.setProgressPercent(total > 0 ? Math.round((finished / (double) total) * 100) : 0L);
            project.setUpdatedAt(new Date());
            projectRepository.save(project);
        });
    }

    private List<InkWeeklyPoint> buildWeekly(Long emId, InkWritingGoal goals) {
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Date from = dateFromLocalDate(monday);
        Date to = endOfDay(dateFromLocalDate(monday.plusDays(6)));
        List<InkDailyEntry> entries = entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(emId, from, to);
        List<InkWeeklyPoint> out = new ArrayList<>();
        SimpleDateFormat labelFormat = new SimpleDateFormat("EEE", Locale.US);
        long dailyWords = Math.max(1L, value(goals.getDailyWords()));
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            String key = dateKey(dateFromLocalDate(day));
            long words = entries.stream()
                    .filter(e -> dateKey(e.getEntryDate()).equals(key))
                    .mapToLong(e -> value(e.getWords()))
                    .sum();
            out.add(InkWeeklyPoint.builder()
                    .date(labelFormat.format(dateFromLocalDate(day)))
                    .words(words)
                    .score(Math.min(100L, Math.round((words / (double) dailyWords) * 100)))
                    .build());
        }
        return out;
    }

    private List<InkCumulativePoint> buildCumulative(Long emId) {
        LocalDate today = LocalDate.now();
        Date from = dateFromLocalDate(LocalDate.of(today.getYear(), 1, 1));
        Date to = endOfDay(dateFromLocalDate(today));
        List<InkDailyEntry> entries = entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(emId, from, to);
        List<InkCumulativePoint> out = new ArrayList<>();
        long running = 0L;
        for (int month = 1; month <= today.getMonthValue(); month++) {
            final int currentMonth = month;
            long words = entries.stream()
                    .filter(e -> toLocalDate(e.getEntryDate()).getMonthValue() == currentMonth)
                    .mapToLong(e -> value(e.getWords()))
                    .sum();
            running += words;
            out.add(InkCumulativePoint.builder()
                    .month(Month.of(month).name().substring(0, 3))
                    .words(running)
                    .build());
        }
        return out;
    }

    private List<InkHeatmapDay> buildHeatmap(Long emId, InkWritingGoal goals) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(180);
        Map<String, InkDailyEntry> entriesByDate = entriesByDate(entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(
                emId,
                dateFromLocalDate(start),
                endOfDay(dateFromLocalDate(today))
        ));
        List<InkHeatmapDay> out = new ArrayList<>();
        for (int i = 0; i <= 180; i++) {
            LocalDate day = start.plusDays(i);
            String key = dateKey(dateFromLocalDate(day));
            InkDailyEntry entry = entriesByDate.get(key);
            out.add(InkHeatmapDay.builder()
                    .date(key)
                    .quality(entry != null ? entry.getQuality() : InkDayQuality.NONE)
                    .build());
        }
        return out;
    }

    private long currentStreak(Long emId) {
        LocalDate cursor = LocalDate.now();
        Set<String> loggedDates = new HashSet<>();
        entryRepository.findByEmIdAndEntryDateBetweenOrderByEntryDateAsc(
                emId,
                dateFromLocalDate(cursor.minusDays(370)),
                endOfDay(dateFromLocalDate(cursor))
        ).stream()
                .filter(e -> value(e.getWords()) > 0 || value(e.getFocusMinutes()) > 0)
                .forEach(e -> loggedDates.add(dateKey(e.getEntryDate())));
        if (!loggedDates.contains(dateKey(dateFromLocalDate(cursor)))) {
            cursor = cursor.minusDays(1);
        }
        long streak = 0L;
        while (loggedDates.contains(dateKey(dateFromLocalDate(cursor)))) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private InkDayQuality toQuality(Long words, Long goal) {
        double ratio = value(words) / (double) Math.max(1L, value(goal));
        if (ratio >= 0.8) return InkDayQuality.GOOD;
        if (ratio >= 0.4) return InkDayQuality.FAIR;
        if (ratio > 0) return InkDayQuality.POOR;
        return InkDayQuality.NONE;
    }

    private InkWritingGoal defaultGoals(Long emId) {
        return InkWritingGoal.builder()
                .emId(emId)
                .dailyWords(1000L)
                .monthlyWords(20000L)
                .dailyFocus(60L)
                .streakTarget(7L)
                .build();
    }

    private InkSettings defaultSettings(Long emId) {
        return InkSettings.builder()
                .emId(emId)
                .wordGoalReminder(true)
                .reminderTime("20:00")
                .autoLogStreak(true)
                .showWordCountInMenu(false)
                .build();
    }

    private Long owner(Long emId) {
        Long ownerId = emId != null ? emId : UserContext.getEmId();
        if (ownerId == null) throw new RuntimeException("Employee context not found");
        return ownerId;
    }

    private String normalizeCover(String cover) {
        if (cover == null || cover.isBlank()) return cover;
        String marker = "/api/v1/files/public/";
        int markerIndex = cover.indexOf(marker);
        if (markerIndex >= 0) return cover.substring(markerIndex + marker.length());

        String publicMarker = "/public/";
        int publicIndex = cover.indexOf(publicMarker);
        if (publicIndex >= 0) return cover.substring(publicIndex + publicMarker.length());

        return cover;
    }

    private long value(Long n) {
        return n != null ? n : 0L;
    }

    private Long defaultLong(Long value, Long fallback) {
        return value != null ? value : fallback;
    }

    private Date startOfDay(Date date) {
        return dateFromLocalDate(toLocalDate(date));
    }

    private Date endOfDay(Date date) {
        return Date.from(toLocalDate(date).plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant());
    }

    private Date dateFromLocalDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String dateKey(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private Map<String, InkDailyEntry> entriesByDate(List<InkDailyEntry> entries) {
        Map<String, InkDailyEntry> out = new HashMap<>();
        entries.forEach(entry -> out.put(dateKey(entry.getEntryDate()), entry));
        return out;
    }
}
