package com.training.librarymanagement.services;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.AccountInputDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.exceptions.AccountModificationException;
import com.training.librarymanagement.exceptions.AccountNotFoundException;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.utils.AccountMapper;
import com.training.librarymanagement.utils.LibraryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {

    private static Logger LOG = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountDTO getAccountById(String id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException());
        return AccountMapper.toDTO(account);
    }

    public void createAccount(AccountInputDTO accountToCreate) {
        Account newAccount = AccountMapper.fromDTO(accountToCreate);
        newAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
        accountRepository.save(newAccount);
    }

    public List<BookDTO> getBooksOwnershipByAccount(String accountId) throws AccountNotFoundException {
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        Set<BookReservation> reservations = account.getBookReservation();
        List<BookDTO> books = new ArrayList<>();
        reservations.stream().forEach(r -> {
            Book reserved = r.getBookItem().getBook();
            books.add(LibraryMapper.toDTO(reserved));
        });
        return books;
    }

    public AccountDTO getAccountByLogin(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
        return AccountMapper.toDTO(account);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(String id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException());
        if (account instanceof Member) {
            accountRepository.changeStatus(id, !((Member) account).getActive());
        } else {
            LOG.error("Changing status to admin: impossible");
            throw new AccountModificationException();
        }
    }
}
