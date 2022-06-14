package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Librarian;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.AccountInputDTO;
import com.training.librarymanagement.enums.AccountType;

import java.util.ArrayList;
import java.util.List;

public class AccountMapper {

    public AccountMapper() {
        // NOTHING
    }

    public static Account fromDTO(AccountInputDTO accountToCreate) {
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
        account.setPassword(accountToCreate.getPassword());

        return account;
    }

    public static AccountDTO toDTO(Account account) {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setFirstName(account.getFirstName());
        accountDTO.setLastName(account.getLastName());
        accountDTO.setUsername(account.getUsername());
        accountDTO.setPassword(account.getPassword());

        if (account instanceof Member) {
            accountDTO.setAccountType(AccountType.MEMBER);
            accountDTO.setActive(((Member) account).getActive());
        } else {
            accountDTO.setAccountType(AccountType.ADMIN);
        }
        return accountDTO;
    }

    public static List<AccountDTO> toDTOs(List<? extends  Account> accounts) {
        List<AccountDTO> dtos = new ArrayList<>();
        accounts.forEach(a -> {
            dtos.add(toDTO(a));
        });
        return dtos;
    }
}
