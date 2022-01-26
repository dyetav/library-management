package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonUtils {

    @Autowired
    protected AccountRepository accountRepository;

    protected Account createAccountMember(String username, String firstName, String lastName) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setUsername(username);
        member.setActive(true);
        return accountRepository.save(member);
    }

}
