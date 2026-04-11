package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.config.RabbitConfig;
import com.springcore.ai.scaiplatform.dto.NotificationDTO;
import com.springcore.ai.scaiplatform.entity.Notification;
import com.springcore.ai.scaiplatform.repository.api.NotificationRepository;
import com.springcore.ai.scaiplatform.security.UserContext;
import com.springcore.ai.scaiplatform.service.api.NotificationService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;
    private final Map<Long, Sinks.Many<NotificationDTO>> userSinks = new ConcurrentHashMap<>();

    public NotificationServiceImpl(RabbitTemplate rabbitTemplate, NotificationRepository notificationRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Flux<NotificationDTO> getNotificationStream(Long userId) {
        if (UserContext.getUserId() == null || userId.compareTo(UserContext.getUserId()) != 0) {
            throw new ValidationException("Invalid user id");
        }

        log.info(">>> User {} is connecting to Notification Stream (SSE)", userId);
        return userSinks.computeIfAbsent(userId, k ->
                        Sinks.many().multicast().onBackpressureBuffer()
                ).asFlux()
                .doOnCancel(() -> {
                    log.info("<<< User {} disconnected from stream", userId);
                    userSinks.remove(userId);
                })
                .doOnTerminate(() -> userSinks.remove(userId));
    }

    @Override
    public void sendToUser(Long userId, NotificationDTO payload) {
        log.info("Sending notification to RabbitMQ for user: {}, title: {}", userId, payload.getTitle());
        rabbitTemplate.convertAndSend(
                RabbitConfig.NOTI_EXCHANGE,
                "noti.user." + userId,
                payload
        );
    }

    @RabbitListener(queues = RabbitConfig.NOTI_QUEUE)
    public void receiveFromRabbit(NotificationDTO payload,
                                  @Header("amqp_receivedRoutingKey") String routingKey) {

        log.info("Message received from RabbitMQ! RoutingKey: {}", routingKey);

        try {
            String userIdStr = routingKey.substring(routingKey.lastIndexOf(".") + 1);
            Long userId = Long.parseLong(userIdStr);
            Sinks.Many<NotificationDTO> sink = userSinks.get(userId);
            if (sink != null) {
                log.info("Pushing notification to User {}'s screen", userId);
                sink.emitNext(payload, Sinks.EmitFailureHandler.FAIL_FAST);
            } else {
                log.warn("User {} has no active SSE connection. Message skipped.", userId);
            }
        } catch (Exception e) {
            log.error("Failed to process message from RabbitMQ: {}", e.getMessage());
        }
    }

    /*@Override
    public void sendToUsers(List<Long> userIds, NotificationDTO payload) {
        userIds.forEach(id -> sendToUser(id, payload));
    }*/

    @Override
    public List<Notification> getHistory(Long userId) {
        return notificationRepository.findTop20ByUserIdOrderByCreateDateDesc(userId);
    }

    @Override
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    public Integer countUnread(Long userId) {
        log.info("Counting unread notifications for user: {}", userId);
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional // สำคัญมากสำหรับการทำ Bulk Update
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
