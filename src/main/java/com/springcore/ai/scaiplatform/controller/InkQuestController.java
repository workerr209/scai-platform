package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.InkQuest.InkDashboardSummary;
import com.springcore.ai.scaiplatform.dto.InkQuest.InkEntrySearchReq;
import com.springcore.ai.scaiplatform.entity.InkQuest.*;
import com.springcore.ai.scaiplatform.service.api.InkQuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/inkquest")
public class InkQuestController {
    private final InkQuestService inkQuestService;

    @Autowired
    public InkQuestController(InkQuestService inkQuestService) {
        this.inkQuestService = inkQuestService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<InkDashboardSummary> dashboard() {
        return ResponseEntity.ok(inkQuestService.getDashboard(null));
    }

    @PostMapping("/projects/search")
    public ResponseEntity<List<InkProject>> searchProjects(@RequestBody(required = false) Map<String, Long> criteria) {
        return ResponseEntity.ok(inkQuestService.searchProjects(readEmId(criteria)));
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<InkProject> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(inkQuestService.getProject(id, null));
    }

    @PostMapping("/projects/save")
    public ResponseEntity<InkProject> saveProject(@RequestBody InkProject form) {
        return ResponseEntity.ok(inkQuestService.saveProject(form));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        boolean deleted = inkQuestService.deleteProject(id, null);
        return deleted ? ResponseEntity.ok(success("Deleted successfully")) : ResponseEntity.notFound().build();
    }

    @GetMapping("/chapters")
    public ResponseEntity<List<InkChapter>> searchChapters(@RequestParam Long projectId) {
        return ResponseEntity.ok(inkQuestService.searchChapters(projectId, null));
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<InkChapter> getChapter(@PathVariable Long id) {
        return ResponseEntity.ok(inkQuestService.getChapter(id, null));
    }

    @PostMapping("/chapters/save")
    public ResponseEntity<InkChapter> saveChapter(@RequestBody InkChapter form) {
        return ResponseEntity.ok(inkQuestService.saveChapter(form));
    }

    @DeleteMapping("/chapters/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        boolean deleted = inkQuestService.deleteChapter(id, null);
        return deleted ? ResponseEntity.ok(success("Deleted successfully")) : ResponseEntity.notFound().build();
    }

    @PostMapping("/entries/search")
    public ResponseEntity<List<InkDailyEntry>> searchEntries(@RequestBody(required = false) InkEntrySearchReq criteria) {
        return ResponseEntity.ok(inkQuestService.searchEntries(criteria != null ? criteria : new InkEntrySearchReq()));
    }

    @GetMapping("/entries/by-date/{date}")
    public ResponseEntity<InkDailyEntry> getEntryByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
        InkDailyEntry entry = inkQuestService.getEntryByDate(date, null);
        return entry != null ? ResponseEntity.ok(entry) : ResponseEntity.notFound().build();
    }

    @PostMapping("/entries/save")
    public ResponseEntity<InkDailyEntry> saveEntry(@RequestBody InkDailyEntry form) {
        return ResponseEntity.ok(inkQuestService.saveEntry(form));
    }

    @GetMapping("/goals")
    public ResponseEntity<InkWritingGoal> getGoals() {
        return ResponseEntity.ok(inkQuestService.getGoals(null));
    }

    @PostMapping("/goals/save")
    public ResponseEntity<InkWritingGoal> saveGoals(@RequestBody InkWritingGoal form) {
        return ResponseEntity.ok(inkQuestService.saveGoals(form));
    }

    @GetMapping("/settings")
    public ResponseEntity<InkSettings> getSettings() {
        return ResponseEntity.ok(inkQuestService.getSettings(null));
    }

    @PostMapping("/settings/save")
    public ResponseEntity<InkSettings> saveSettings(@RequestBody InkSettings form) {
        return ResponseEntity.ok(inkQuestService.saveSettings(form));
    }

    @GetMapping("/notes")
    public ResponseEntity<List<InkNote>> searchNotes() {
        return ResponseEntity.ok(inkQuestService.searchNotes(null));
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<InkNote> getNote(@PathVariable Long id) {
        return ResponseEntity.ok(inkQuestService.getNote(id, null));
    }

    @PostMapping("/notes/save")
    public ResponseEntity<InkNote> saveNote(@RequestBody InkNote form) {
        return ResponseEntity.ok(inkQuestService.saveNote(form));
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        boolean deleted = inkQuestService.deleteNote(id, null);
        return deleted ? ResponseEntity.ok(success("Deleted successfully")) : ResponseEntity.notFound().build();
    }

    private Long readEmId(Map<String, Long> criteria) {
        return criteria != null ? criteria.get("emId") : null;
    }

    private Map<String, Object> success(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        return map;
    }
}
