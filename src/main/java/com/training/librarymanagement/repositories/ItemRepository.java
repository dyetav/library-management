package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<BookItem, String> {
}
