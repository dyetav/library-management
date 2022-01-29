package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<BookItem, String> {

    Set<BookItem> findByBook(Book book);
}
