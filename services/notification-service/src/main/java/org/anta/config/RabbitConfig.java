package org.anta.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange("notifications-exchange");
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("notifications.email.queue").build();
    }

    @Bean
    public Binding bindEmailQueue(Queue emailQueue, TopicExchange notificationsExchange) {
        return BindingBuilder.bind(emailQueue).to(notificationsExchange).with("notifications.email");
    }
}

