package com.training.librarymanagement.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class LibraryManagementConfiguration {

    @Value("${books.reservation.days.max}")
    private int returnDays;

    @Value("${books.reservation.fine.price}")
    private BigDecimal fineAmount;

    public int getReturnDays() {
        return returnDays;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }
}
