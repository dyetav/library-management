package com.training.librarymanagement.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Configuration
public class LibraryManagementConfiguration {

    @Value("${books.reservation.days.max}")
    private int returnDays;

    @Value("${books.reservation.fine.price}")
    private BigDecimal fineAmount;

    @Value("${library.notification.server.port}")
    private int notificationServerPort;

    public int getReturnDays() {
        return returnDays;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    @Bean
    public WebClient notificationWebClient() {
        return WebClient.create("http://localhost:" + notificationServerPort);
    }
}
