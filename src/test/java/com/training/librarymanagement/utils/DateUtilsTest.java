package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateUtilsTest {

    @Test
    public void testCheckouting() {
        ReservationInputDTO reservationInput = new ReservationInputDTO(Date.from(Instant.now()), Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)));
        assertTrue(DateUtils.isCheckouting(reservationInput));

        assertTrue(DateUtils.isCheckouting(null));

        reservationInput = new ReservationInputDTO(Date.from(Instant.now()), null);
        assertTrue(DateUtils.isCheckouting(reservationInput));

        reservationInput = new ReservationInputDTO(null, null);
        assertTrue(DateUtils.isCheckouting(reservationInput));

        reservationInput = new ReservationInputDTO(null, Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)));
        assertTrue(DateUtils.isCheckouting(reservationInput));

        reservationInput = new ReservationInputDTO(Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(2L, ChronoUnit.DAYS)));
        assertFalse(DateUtils.isCheckouting(reservationInput));

        Date startDate = Date.from(LocalDateTime.now().atZone(ZoneId.of("UTC")).plus(1L, ChronoUnit.DAYS).with(LocalTime.MIN).toInstant());
        Date endDate = Date.from(Instant.now().plus(2L, ChronoUnit.DAYS));
        reservationInput = new ReservationInputDTO(startDate, endDate);
        assertFalse(DateUtils.isCheckouting(reservationInput));

        startDate = Date.from(LocalDateTime.now().atZone(ZoneId.of("UTC")).plus(1L, ChronoUnit.DAYS).with(LocalTime.MIN).toInstant());
        endDate = Date.from(startDate.toInstant().plus(2L, ChronoUnit.HOURS));
        reservationInput = new ReservationInputDTO(startDate, endDate);
        assertFalse(DateUtils.isCheckouting(reservationInput));
    }
}
