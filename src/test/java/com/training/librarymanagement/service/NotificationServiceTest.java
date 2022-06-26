package com.training.librarymanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.training.librarymanagement.configuration.NotificationConfiguration;
import com.training.librarymanagement.entities.dtos.NotificationDTO;
import com.training.librarymanagement.services.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private NotificationConfiguration configuration;

    @Test
    public void testNotificationServiceSend_Success() throws JsonProcessingException {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setMessage("Rabbit message");
        notificationDTO.setAccountId("abc123");
        doNothing().when(rabbitTemplate).send(anyString(), anyString(), any(Message.class));
        when(configuration.getNotificationExchange()).thenReturn("fakeExchange");
        when(configuration.getNotificationRoutingKey()).thenReturn("fakeRoutingKey");
        Assertions.assertDoesNotThrow(() -> notificationService.send(notificationDTO));
    }

}
