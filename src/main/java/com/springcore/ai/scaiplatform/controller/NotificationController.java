package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.NotificationDTO;
import com.springcore.ai.scaiplatform.entity.Notification;
import com.springcore.ai.scaiplatform.service.api.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(path = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationDTO> stream(@PathVariable Long userId) {
        return notificationService.getNotificationStream(userId);
    }

    @GetMapping("/history/{userId}")
    public List<Notification> getHistory(@PathVariable Long userId) {
        return notificationService.getHistory(userId);
    }

    @PatchMapping("/read/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnread(userId));
    }

    @PatchMapping("/read-all/{userId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}