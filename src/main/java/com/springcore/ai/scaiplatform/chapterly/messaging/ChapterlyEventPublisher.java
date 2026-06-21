package com.springcore.ai.scaiplatform.chapterly.messaging;

import com.springcore.ai.scaiplatform.chapterly.config.ChapterlyMessagingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChapterlyEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAfterCommit(
            String routingKey,
            Long actorUserId,
            Long targetUserId,
            String aggregateType,
            Long aggregateId,
            Map<String, Object> payload
    ) {
        ChapterlyEvent event = ChapterlyEvent.builder()
                .eventName(routingKey)
                .actorUserId(actorUserId)
                .targetUserId(targetUserId)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload == null ? Map.of() : payload)
                .occurredAt(Instant.now())
                .build();

        Runnable publish = () -> rabbitTemplate.convertAndSend(
                ChapterlyMessagingConfig.EXCHANGE,
                routingKey,
                event
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish.run();
                }
            });
            return;
        }

        publish.run();
    }
}
