package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.AccountInputDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.exceptions.AccountNotFoundException;
import com.training.librarymanagement.services.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/library-management/api/account")
@Api(value = "AccountManagement")
public class AccountController {

    private static Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "Get an account by its id", tags = {"account"})
    @GetMapping("/v1/accounts/{id}")
    public AccountDTO getAccountById(@PathVariable("id") String id) throws AccountNotFoundException {
        LOG.info("Calling get account by id with param id {}", id);
        AccountDTO account = accountService.getAccountById(id);
        return account;
    }

    // TODO: only ADMIN
    // TODO: ----------
    @ApiOperation(value = "Get the reserved books by account id", tags = {"account"})
    @GetMapping("/v1/accounts/{id}/books")
    public List<BookDTO> getBooksOwnershipByAccount(@PathVariable("id") String accountId) throws AccountNotFoundException {
        LOG.info("Get the reserved books by account {}", accountId);
        return accountService.getBooksOwnershipByAccount(accountId);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "Create a new account by registering it", tags = {"security"})
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@RequestBody AccountInputDTO user) {
        accountService.createAccount(user);
    }

}
