package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.dtos.BookItemNextReservationDTO;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

public class LibraryUtils {

    public LibraryUtils() {
        // NOTHING
    }

    /**
     * Return true if a book item is available depending on the reservation input
     * A book item is available if:
     * - no reservation has been initiated for the item
     * - the item is free from reservation in the selected reservation dates
     *
     * @param bookItems the book items
     * @param reservationInput the reservation with wished start and end date
     * @return
     */
    public static boolean isBookAvailable(Set<BookItem> bookItems, ReservationInputDTO reservationInput) {
        return bookItems.stream().anyMatch(bookItemAvailable(reservationInput));
    }

    /**
     * Pick up the first available item by reservation date
     *
     * @param bookItems
     * @param reservationInput
     * @return
     */
    public static BookItem getAvailableBookItemByReservation(Set<BookItem> bookItems, ReservationInputDTO reservationInput) {
        return bookItems
            .stream()
            .filter(bookItemAvailable(reservationInput))
            .findFirst()
            .get();
    }

    private static Predicate<BookItem> bookItemAvailable(ReservationInputDTO reservationInput) {
        Predicate<BookItem> bookItemAvailable = b -> b.getBookReservations().isEmpty() ||
            (reservationInput != null &&
                reservationInput.getWishedStartDate() != null &&
                b.getBookReservations().stream().filter(br -> br.getEndBookingDate().before(reservationInput.getWishedStartDate())).count() > 0) ||
            (reservationInput != null &&
                reservationInput.getWishedEndDate() != null &&
                b.getBookReservations().stream().filter(br -> br.getStartBookingDate().after(reservationInput.getWishedEndDate())).count() == b.getBookReservations().size());
        return bookItemAvailable;
    }
}
