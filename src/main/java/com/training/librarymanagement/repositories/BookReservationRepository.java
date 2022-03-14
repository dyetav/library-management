package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BookReservationRepository extends JpaRepository<BookReservation, String> {

    Set<BookReservation> findByBookItem(BookItem bookItem);

    Set<BookReservation> findByAccount(Account account);

    @Query(value = "select a " +
        "from Account a inner join a.bookReservation br " +
        "where br.bookItem.code in :ids")
    List<Account> findOwnersByBookItemIds(@Param("ids") List<String> bookItemIds);


}
