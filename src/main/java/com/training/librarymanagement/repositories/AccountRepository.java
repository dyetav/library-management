package com.training.librarymanagement.repositories;

import com.training.librarymanagement.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByUsername(String username);

    @Modifying
    @Query(value = "UPDATE account " +
        "SET active = :value " +
        "WHERE id = :id", nativeQuery = true)
    void changeStatus(@Param("id") String id, @Param("value") Boolean value);

    @Modifying
    @Query(value = "UPDATE account " +
        "SET account_type = :roleTo " +
        "WHERE id = :id", nativeQuery = true)
    void changeRole(@Param("id") String accountId, @Param("roleTo") String roleTo);
}
