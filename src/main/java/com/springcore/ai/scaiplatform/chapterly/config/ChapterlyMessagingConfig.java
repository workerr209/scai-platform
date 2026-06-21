package com.springcore.ai.scaiplatform.chapterly.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.aopalliance.aop.Advice;

@Configuration
public class ChapterlyMessagingConfig {
    public static final String EXCHANGE = "chapterly.events";
    public static final String DEAD_LETTER_EXCHANGE = "chapterly.events.dlx";

    public static final String NOTIFICATION_QUEUE = "chapterly.notification.events";
    public static final String INBOX_QUEUE = "chapterly.inbox.events";
    public static final String COMMENT_QUEUE = "chapterly.comment.events";

    public static final String NOTIFICATION_DLQ = "chapterly.notification.events.dlq";
    public static final String INBOX_DLQ = "chapterly.inbox.events.dlq";
    public static final String COMMENT_DLQ = "chapterly.comment.events.dlq";

    public static final String INBOX_MESSAGE_CREATED = "chapterly.inbox.message.created";
    public static final String INBOX_MESSAGE_READ = "chapterly.inbox.message.read";
    public static final String COMMENT_CREATED = "chapterly.comment.created";
    public static final String COMMENT_REPLIED = "chapterly.comment.replied";
    public static final String NOTIFICATION_CREATED = "chapterly.notification.created";
    public static final String STORY_PUBLISHED = "chapterly.story.published";
    public static final String CHAPTER_PUBLISHED = "chapterly.chapter.published";

    @Bean
    public TopicExchange chapterlyEventsExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange chapterlyDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue chapterlyNotificationQueue() {
        return durableQueue(NOTIFICATION_QUEUE, NOTIFICATION_DLQ);
    }

    @Bean
    public Queue chapterlyInboxQueue() {
        return durableQueue(INBOX_QUEUE, INBOX_DLQ);
    }

    @Bean
    public Queue chapterlyCommentQueue() {
        return durableQueue(COMMENT_QUEUE, COMMENT_DLQ);
    }

    @Bean
    public Queue chapterlyNotificationDeadLetterQueue() {
        return QueueBuilder.durable(NOTIFICATION_DLQ).build();
    }

    @Bean
    public Queue chapterlyInboxDeadLetterQueue() {
        return QueueBuilder.durable(INBOX_DLQ).build();
    }

    @Bean
    public Queue chapterlyCommentDeadLetterQueue() {
        return QueueBuilder.durable(COMMENT_DLQ).build();
    }

    @Bean
    public Binding chapterlyNotificationBinding(
            @Qualifier("chapterlyNotificationQueue") Queue chapterlyNotificationQueue,
            @Qualifier("chapterlyEventsExchange") TopicExchange chapterlyEventsExchange
    ) {
        return BindingBuilder.bind(chapterlyNotificationQueue)
                .to(chapterlyEventsExchange)
                .with("chapterly.notification.#");
    }

    @Bean
    public Binding chapterlyStoryPublishedNotificationBinding(
            @Qualifier("chapterlyNotificationQueue") Queue chapterlyNotificationQueue,
            @Qualifier("chapterlyEventsExchange") TopicExchange chapterlyEventsExchange
    ) {
        return BindingBuilder.bind(chapterlyNotificationQueue)
                .to(chapterlyEventsExchange)
                .with(STORY_PUBLISHED);
    }

    @Bean
    public Binding chapterlyChapterPublishedNotificationBinding(
            @Qualifier("chapterlyNotificationQueue") Queue chapterlyNotificationQueue,
            @Qualifier("chapterlyEventsExchange") TopicExchange chapterlyEventsExchange
    ) {
        return BindingBuilder.bind(chapterlyNotificationQueue)
                .to(chapterlyEventsExchange)
                .with(CHAPTER_PUBLISHED);
    }

    @Bean
    public Binding chapterlyInboxBinding(
            @Qualifier("chapterlyInboxQueue") Queue chapterlyInboxQueue,
            @Qualifier("chapterlyEventsExchange") TopicExchange chapterlyEventsExchange
    ) {
        return BindingBuilder.bind(chapterlyInboxQueue)
                .to(chapterlyEventsExchange)
                .with("chapterly.inbox.#");
    }

    @Bean
    public Binding chapterlyCommentBinding(
            @Qualifier("chapterlyCommentQueue") Queue chapterlyCommentQueue,
            @Qualifier("chapterlyEventsExchange") TopicExchange chapterlyEventsExchange
    ) {
        return BindingBuilder.bind(chapterlyCommentQueue)
                .to(chapterlyEventsExchange)
                .with("chapterly.comment.#");
    }

    @Bean
    public Binding chapterlyNotificationDeadLetterBinding(
            @Qualifier("chapterlyNotificationDeadLetterQueue") Queue chapterlyNotificationDeadLetterQueue,
            @Qualifier("chapterlyDeadLetterExchange") DirectExchange chapterlyDeadLetterExchange
    ) {
        return BindingBuilder.bind(chapterlyNotificationDeadLetterQueue)
                .to(chapterlyDeadLetterExchange)
                .with(NOTIFICATION_DLQ);
    }

    @Bean
    public Binding chapterlyInboxDeadLetterBinding(
            @Qualifier("chapterlyInboxDeadLetterQueue") Queue chapterlyInboxDeadLetterQueue,
            @Qualifier("chapterlyDeadLetterExchange") DirectExchange chapterlyDeadLetterExchange
    ) {
        return BindingBuilder.bind(chapterlyInboxDeadLetterQueue)
                .to(chapterlyDeadLetterExchange)
                .with(INBOX_DLQ);
    }

    @Bean
    public Binding chapterlyCommentDeadLetterBinding(
            @Qualifier("chapterlyCommentDeadLetterQueue") Queue chapterlyCommentDeadLetterQueue,
            @Qualifier("chapterlyDeadLetterExchange") DirectExchange chapterlyDeadLetterExchange
    ) {
        return BindingBuilder.bind(chapterlyCommentDeadLetterQueue)
                .to(chapterlyDeadLetterExchange)
                .with(COMMENT_DLQ);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory chapterlyRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("chapterlyRetryInterceptor") Advice chapterlyRetryInterceptor
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(chapterlyRetryInterceptor);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public Advice chapterlyRetryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    private Queue durableQueue(String queueName, String deadLetterRoutingKey) {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey)
                .build();
    }
}
