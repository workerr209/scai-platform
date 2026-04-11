package com.springcore.ai.scaiplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String NOTI_EXCHANGE = "noti.exchange";
    public static final String NOTI_QUEUE = "noti.queue";
    public static final String ROUTING_KEY_PATTERN = "noti.user.#";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(NOTI_EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(NOTI_QUEUE, true); // Durable = คิวไม่หายเมื่อ restart
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_PATTERN);
    }

    // ช่วยให้ส่ง Object เป็น JSON เข้าคิวได้เลย ไม่ต้องแปลงเอง
    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
