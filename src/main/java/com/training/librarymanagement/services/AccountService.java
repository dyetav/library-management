package com.training.librarymanagement.services;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Librarian;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.AccountInputDTO;
import com.training.librarymanagement.enums.AccountType;
import com.training.librarymanagement.exceptions.AccountNotFoundException;
import com.training.librarymanagement.repositories.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private static Logger LOG = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    public AccountDTO getAccountById(String id) throws AccountNotFoundException {
        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException());
        return toDTO(account);
    }

    public void createAccount(AccountInputDTO accountToCreate) {
        Account newAccount = fromDTO(accountToCreate);
        accountRepository.save(newAccount);
    }

    private Account fromDTO(AccountInputDTO accountToCreate) {
        Account account;
        if (accountToCreate.getType().equals(AccountType.MEMBER)) {
            account = new Member();
            ((Member) account).setActive(accountToCreate.getActive());
        } else {
            account = new Librarian();
        }
        account.setLastName(accountToCreate.getLastName());
        account.setFirstName(accountToCreate.getFirstName());
        account.setUsername(accountToCreate.getUsername());

        return account;
    }

    private AccountDTO toDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setFirstName(account.getFirstName());
        accountDTO.setLastName(account.getLastName());
        accountDTO.setUsername(account.getUsername());

        if (account instanceof Member) {
            accountDTO.setActive(((Member) account).getActive());
        }
        return accountDTO;
    }
}
