package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.dtos.AccountInputDTO;
import com.training.librarymanagement.services.AccountService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/library-management")
public class SecurityController {

    private static Logger LOG = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private AccountService accountService;

    @PostMapping("/signup")
    @ApiOperation(value = "Create a new account by registering it", tags = {"security"})
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@RequestBody AccountInputDTO user) {
        accountService.createAccount(user);
    }
}
