package com.training.librarymanagement.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.librarymanagement.configuration.NotificationConfiguration;
import com.training.librarymanagement.entities.dtos.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationService {

    private static Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    @Qualifier("notificationWebClient")
    private WebClient webClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationConfiguration configuration;

    public void send(NotificationDTO notification) throws JsonProcessingException {
        Message message = new Message(mapper.writeValueAsBytes(notification));
        rabbitTemplate.send(configuration.getNotificationExchange(), configuration.getNotificationRoutingKey(), message);
    }

    public List<NotificationDTO> getNotificationsByAccountId(String accountId) {
        NotificationDTO[] notifications = webClient
            .get()
            .uri("/library-notification/api/notifications/v1/accounts/" + accountId)
            .header("Content-Type", "application/json;charset=UTF-8")
            .retrieve()
            .bodyToMono(NotificationDTO[].class)
            .block();

        return Arrays.asList(notifications);
    }

    public String justAPing() {
        return webClient
            .get()
            .uri("/library-notification/api/notifications/ping")
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
