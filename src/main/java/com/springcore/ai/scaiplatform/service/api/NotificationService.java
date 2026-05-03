package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.NotificationDTO;
import com.springcore.ai.scaiplatform.entity.Notification;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface NotificationService {
    Flux<ServerSentEvent<NotificationDTO>> getNotificationStream(Long userId);

    void sendToUser(Long userId, NotificationDTO payload);

    // void sendToUsers(List<Long> userIds, NotificationDTO payload);

    List<Notification> getHistory(Long userId);

    void markAsRead(Long id);

    Integer countUnread(Long userId);

    void markAllAsRead(Long userId);

}
