package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, String> {

}
