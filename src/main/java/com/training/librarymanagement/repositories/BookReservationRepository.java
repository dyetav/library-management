package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface BookReservationRepository extends JpaRepository<BookReservation, String> {

    Set<BookReservation> findByBookItem(BookItem bookItem);

    Set<BookReservation> findByAccount(Account account);
}
