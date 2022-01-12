package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.repositories.AccountRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(value = SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void getAccountById_Success() {
        Account member = createAccountMember("dietav", "Diego", "Tavolaro");
        Member testedMember = RestAssured.given().port(port).pathParam("id", member.getId())
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(200)
            .extract().as(Member.class);

        assertEquals("dietav", testedMember.getUsername());
        assertTrue(testedMember.getActive());
        assertEquals("Diego", testedMember.getFirstName());
        assertEquals("Tavolaro", testedMember.getLastName());
    }

    @Test
    public void getAccountById_NotFound() {
        RestAssured.given().port(port).pathParam("id", "NOT_EXISTING")
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(404);
    }

    private Account createAccountMember(String username, String firstName, String lastName) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setUsername(username);
        member.setActive(true);
        return accountRepository.save(member);
    }
}
