package com.training.librarymanagement.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
public class LibraryManagementConfiguration {

    @Value("${books.reservation.days.max}")
    private int returnDays;

    @Value("${books.reservation.fine.price}")
    private BigDecimal fineAmount;

    @Value("${library.notification.server.address:localhost}")
    private String notificationServerAddress;

    @Value("${library.notification.server.port:8889}")
    private int notificationServerPort;

    @Autowired
    private DataSource dataSource;

    public int getReturnDays() {
        return returnDays;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    @Bean
    public WebClient notificationWebClient() {
        return WebClient.create("http://" + notificationServerAddress + ":" + notificationServerPort);
    }
}
