package com.springcore.ai.scaiplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async execution configuration.
 * <p>
 * Centralizes all async-related beans so that SecurityConfig stays focused
 * on security concerns only (Single Responsibility Principle).
 * <p>
 * {@code @EnableAsync} activates Spring's annotation-driven async processing.
 * Without it, {@code @Async} annotations on service methods are silently ignored.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Core thread pool used by all {@code @Async} methods in the application.
     * <p>
     * Sizing rationale:
     *   - corePoolSize  10 : threads kept alive even when idle (base capacity)
     *   - maxPoolSize   20 : upper bound when queue is full
     *   - queueCapacity 500: tasks buffered before new threads beyond core are created
     * <p>
     * Bean name "threadPoolTaskExecutor" is intentional — Spring picks this bean
     * automatically when resolving async executors.
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("ScaiAsync-");
        // Ensure threads finish their current task before shutdown instead of being killed
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    /**
     * Wraps the core executor so that the Spring Security {@code SecurityContext}
     * (authenticated user, roles) is propagated into spawned async threads.
     * <p>
     * Without this wrapper, {@code SecurityContextHolder.getContext()} inside
     * an {@code @Async} method would return an empty, unauthenticated context.
     * <p>
     * Named "taskExecutor" so that Spring's {@code AsyncAnnotationBeanPostProcessor}
     * resolves it as the default executor for {@code @Async} calls.
     */
    @Bean
    public Executor taskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor);
    }
}