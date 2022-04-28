package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import com.training.librarymanagement.enums.Availability;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryUtilsTest {

    @Test
    public void testAvailability() {
        BookItem b1 = createBookItem("AAA");
        BookItem b2 = createBookItem("BBB");
        BookItem b3 = createBookItem("CCC");
        createReservation(b3, Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(2L, ChronoUnit.DAYS)), Availability.RESERVED);
        Set<BookItem> bookItems = Set.of(b1, b2, b3);

        assertTrue(LibraryUtils.isBookAvailable(bookItems, null));

        b1 = createBookItem("AAA");
        createReservation(b1, Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(2L, ChronoUnit.DAYS)), Availability.RESERVED);
        b2 = createBookItem("BBB");
        createReservation(b2, Date.from(Instant.now().plus(2L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(5L, ChronoUnit.DAYS)), Availability.RESERVED);
        b3 = createBookItem("CCC");
        createReservation(b3, Date.from(Instant.now()), Date.from(Instant.now().plus(6L, ChronoUnit.DAYS)), Availability.ON_LOAN);
        bookItems = Set.of(b1, b2, b3);

        ReservationInputDTO reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(3L, ChronoUnit.DAYS))
        );

        assertFalse(LibraryUtils.isBookAvailable(bookItems, reservationInputDTO));

        reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(7L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(10L, ChronoUnit.DAYS))
        );

        assertTrue(LibraryUtils.isBookAvailable(bookItems, reservationInputDTO));

        b1 = createBookItem("AAA");
        createReservation(b1, Date.from(Instant.now().plus(11L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(21L, ChronoUnit.DAYS)), Availability.RESERVED);
        b2 = createBookItem("BBB");
        createReservation(b2, Date.from(Instant.now().plus(13L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(23L, ChronoUnit.DAYS)), Availability.RESERVED);
        b3 = createBookItem("CCC");
        createReservation(b2, Date.from(Instant.now().plus(15L, ChronoUnit.DAYS)), Date.from(Instant.now().plus(25L, ChronoUnit.DAYS)), Availability.RESERVED);
        bookItems = Set.of(b1, b2, b3);

        reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now()),
            Date.from(Instant.now().plus(10L, ChronoUnit.DAYS))
        );

        assertTrue(LibraryUtils.isBookAvailable(bookItems, reservationInputDTO));


    }

    private BookItem createBookItem(String code) {
        BookItem bookItem = new BookItem();
        bookItem.setCode(code);
        return bookItem;
    }

    private void createReservation(BookItem bookItem, Date wishedStartDate, Date wishedEndDate, Availability availability) {
        BookReservation bookReservation = new BookReservation();
        bookReservation.setBookItem(bookItem);
        bookReservation.setAvailability(availability);
        bookReservation.setEndBookingDate(wishedEndDate);
        bookReservation.setStartBookingDate(wishedStartDate);
        bookItem.getBookReservations().add(bookReservation);

    }
}
