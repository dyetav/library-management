package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import com.training.librarymanagement.enums.Availability;

import java.util.Set;
import java.util.function.Predicate;

public class LibraryUtils {

    public LibraryUtils() {
        // NOTHING
    }

    public static boolean isBookAvailable(Set<BookItem> bookItems, ReservationInputDTO reservationInput) {
        Predicate<BookItem> bookItemAvailable = b -> b.getAvailablity().equals(Availability.AVAILABLE) ||
            (reservationInput != null &&
                reservationInput.getWishedStartDate() != null &&
                b.getBookReservations().stream().filter(br -> br.getEndBookingDate().before(reservationInput.getWishedStartDate())).count() > 0) ||
            (reservationInput != null &&
                reservationInput.getWishedEndDate() != null &&
                b.getBookReservations().stream().filter(br -> br.getStartBookingDate().after(reservationInput.getWishedEndDate())).count() == b.getBookReservations().size());
        return bookItems.stream().anyMatch(bookItemAvailable);
    }
}
