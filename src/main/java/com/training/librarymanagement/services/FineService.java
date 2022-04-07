package com.training.librarymanagement.services;

import com.training.librarymanagement.configuration.LibraryManagementConfiguration;
import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Fine;
import com.training.librarymanagement.enums.FineStatus;
import com.training.librarymanagement.exceptions.AccountNotFoundException;
import com.training.librarymanagement.exceptions.BookNotFoundException;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.repositories.FineRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FineService {
    private static Logger LOG = LoggerFactory.getLogger(FineService.class);

    @Autowired
    private LibraryManagementConfiguration conf;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    public void createFine(String isbn, String accountId) throws BookNotFoundException, AccountNotFoundException {
        libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        BigDecimal fineAmount = conf.getFineAmount();

        Fine fine = new Fine();
        fine.setAccount(account);
        fine.setFineStatus(FineStatus.PENDING);
        fine.setPrice(fineAmount);
        fineRepository.save(fine);

    }
}
