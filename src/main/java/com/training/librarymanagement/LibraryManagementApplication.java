package com.training.librarymanagement;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Librarian;
import com.training.librarymanagement.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class LibraryManagementApplication {

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void initializeDatabaseWithFirstAdminUser() {
        AccountRepository accountRepository = context.getBean(AccountRepository.class);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        Optional<Account> adminAccountOpt = accountRepository.findByUsername("firstAdmin");
        if (!adminAccountOpt.isPresent()) {
            Account adminAccount = new Librarian();
            adminAccount.setFirstName("admin");
            adminAccount.setLastName("admin");
            adminAccount.setPassword(passwordEncoder.encode("adminPassword"));
            adminAccount.setUsername("firstAdmin");
            accountRepository.save(adminAccount);
        }
    }

}
