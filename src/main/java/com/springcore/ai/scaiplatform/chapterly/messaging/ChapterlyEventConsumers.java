package com.springcore.ai.scaiplatform.chapterly.messaging;

import com.springcore.ai.scaiplatform.chapterly.config.ChapterlyMessagingConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChapterlyEventConsumers {

    @RabbitListener(
            queues = ChapterlyMessagingConfig.NOTIFICATION_QUEUE,
            containerFactory = "chapterlyRabbitListenerContainerFactory"
    )
    public void handleNotificationEvent(
            ChapterlyEvent event,
            @Header("amqp_receivedRoutingKey") String routingKey
    ) {
        log.info("Chapterly notification event received: routingKey={}, aggregateType={}, aggregateId={}",
                routingKey,
                event.getAggregateType(),
                event.getAggregateId());
        // Notification fan-out rules will be expanded when notification UI/API lands.
    }

    @RabbitListener(
            queues = ChapterlyMessagingConfig.INBOX_QUEUE,
            containerFactory = "chapterlyRabbitListenerContainerFactory"
    )
    public void handleInboxEvent(
            ChapterlyEvent event,
            @Header("amqp_receivedRoutingKey") String routingKey
    ) {
        log.info("Chapterly inbox event received: routingKey={}, aggregateType={}, aggregateId={}",
                routingKey,
                event.getAggregateType(),
                event.getAggregateId());
        // Inbox side effects stay async so REST message writes do not block on counters.
    }

    @RabbitListener(
            queues = ChapterlyMessagingConfig.COMMENT_QUEUE,
            containerFactory = "chapterlyRabbitListenerContainerFactory"
    )
    public void handleCommentEvent(
            ChapterlyEvent event,
            @Header("amqp_receivedRoutingKey") String routingKey
    ) {
        log.info("Chapterly comment event received: routingKey={}, aggregateType={}, aggregateId={}",
                routingKey,
                event.getAggregateType(),
                event.getAggregateId());
        // Comment notification fan-out will attach here when comments land.
    }
}
