package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.dtos.AccountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BookReservationRepository extends JpaRepository<BookReservation, String> {

    Set<BookReservation> findByBookItem(BookItem bookItem);

    Set<BookReservation> findByAccount(Account account);

    @Query(value = "select new com.training.librarymanagement.entities.dtos.AccountDTO(a.username, a.firstName, a.lastName, a.active) " +
        "from Member a inner join a.bookReservation br " +
        "where br.bookItem.code in :ids")
    List<AccountDTO> findOwnersByBookItemIds(@Param("ids") List<String> bookItemIds);
}
