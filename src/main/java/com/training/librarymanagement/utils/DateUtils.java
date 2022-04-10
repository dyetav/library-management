package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.dtos.ReservationInputDTO;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtils {

    public DateUtils() {
        // NOTHING
    }

    public static boolean isCheckouting(ReservationInputDTO reservationInput) {
        Instant now = Instant.now();
        if (reservationInput != null &&
            reservationInput.getWishedStartDate() != null &&
            reservationInput.getWishedStartDate().after(Date.from(now)) &&
            ChronoUnit.DAYS.between(now.truncatedTo(ChronoUnit.DAYS), reservationInput.getWishedStartDate().toInstant().truncatedTo(ChronoUnit.DAYS)) > 0) {

            return false;
        }
        return true;
    }
}
