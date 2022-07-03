package com.training.librarymanagement.jwt;

import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.enums.AccountType;
import com.training.librarymanagement.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDTO account = accountService.getAccountByLogin(username);
        String role = account.getAccountType().equals(AccountType.ADMIN) ? "ROLE_ADMIN" : "ROLE_MEMBER";
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return new User(account.getUsername(), account.getPassword(), authorities);
    }
}
