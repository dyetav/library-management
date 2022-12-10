package com.training.librarymanagement.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationQueueConfiguration {

    @Value("${library.notification.exchange}")
    private String notificationExchange;

    @Value("${library.notification.queue}")
    private String notificationQueue;

    @Value("${notification.routing.key}")
    private String notificationRoutingKey;

    @Value("${library.notification.queue.server.address:localhost}")
    private String serverAddress;

    @Value("${library.notification.queue.server.port:8889}")
    private int port;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(serverAddress);
        connectionFactory.setPort(port);
        connectionFactory.setUsername("diego");
        connectionFactory.setPassword("pippo1234!");
        return connectionFactory;
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(notificationQueue);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(notificationExchange);
    }

    @Bean
    public Binding bind() {
        return BindingBuilder
            .bind(notificationQueue())
            .to(notificationExchange())
            .with(notificationRoutingKey);
    }

    public String getNotificationExchange() {
        return notificationExchange;
    }

    public String getNotificationQueue() {
        return notificationQueue;
    }

    public String getNotificationRoutingKey() {
        return notificationRoutingKey;
    }

}
