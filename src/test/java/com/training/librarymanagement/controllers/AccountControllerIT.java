package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.Librarian;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.entities.dtos.AccountInputDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.enums.AccountType;
import com.training.librarymanagement.jwt.AuthenticationRequest;
import com.training.librarymanagement.utils.CommonTestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(value = SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIT extends CommonTestUtils {

    @LocalServerPort
    private int port;

    private String tokenMember;
    private String tokenAdmin;
    private Account member;
    private Account admin;

    @BeforeEach
    public void before() {
        clearAllRepositories();
        member = createAccount("dietav", "password", "Diego", "Tavolaro", true);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("dietav", "password");
        tokenMember = RestAssured.given().port(port)
            .body(authenticationRequest)
            .expect()
            .when().post("/library-management/signin")
            .then().assertThat().statusCode(200)
            .extract().header("Authorization");

        admin = createAccount("giutav", "password", "Giulia", "Tavolaro", null);
        authenticationRequest = new AuthenticationRequest("giutav", "password");
        tokenAdmin = RestAssured.given().port(port)
            .body(authenticationRequest)
            .expect()
            .when().post("/library-management/signin")
            .then().assertThat().statusCode(200)
            .extract().header("Authorization");
    }

    @AfterEach
    public void after() {
        clearAllRepositories();
    }

    @Test
    public void testGetAccountById_Success() {
        Member testedMember = RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(200)
            .extract().as(Member.class);

        assertEquals("giutav", testedMember.getUsername());
        assertEquals("Giulia", testedMember.getFirstName());
        assertEquals("Tavolaro", testedMember.getLastName());
    }

    @Test
    public void testChangingStatusMemberAccount() {
        assertTrue(((Member) member).getActive());

        RestAssured.given().port(port).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .when().put("/library-management/api/account/v1/accounts/{id}/status")
            .then().assertThat().statusCode(204);

        Member testedMember = RestAssured.given().port(port).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(200)
            .extract().as(Member.class);

        assertFalse(testedMember.getActive());
    }

    @Test
    public void testChangingStatusAdminAccount_Failure() {
        RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .when().put("/library-management/api/account/v1/accounts/{id}/status")
            .then().assertThat().statusCode(400);

    }

    @Test
    public void testChangingStatusAdminAccount_UnauthorizedRequest() {
        RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .when().put("/library-management/api/account/v1/accounts/{id}/status")
            .then().assertThat().statusCode(403);

    }

    @Test
    public void testGetAccountByUsername_Success() {
        Member testedMember = RestAssured.given().port(port).pathParam("username", admin.getUsername())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/username/{username}")
            .then().assertThat().statusCode(200)
            .extract().as(Member.class);

        assertEquals("giutav", testedMember.getUsername());
        assertEquals("Giulia", testedMember.getFirstName());
        assertEquals("Tavolaro", testedMember.getLastName());
    }

    @Test
    public void testCreateAccount_WithSameUsername_Failure() {
        AccountInputDTO newAccountToCreate = new AccountInputDTO();
        newAccountToCreate.setType(AccountType.MEMBER);
        newAccountToCreate.setActive(true);
        newAccountToCreate.setFirstName("Diego");
        newAccountToCreate.setLastName("T");
        newAccountToCreate.setUsername("dietav");
        newAccountToCreate.setPassword("passwordSecondUser");
        RestAssured.given().port(port).body(newAccountToCreate)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/account/signup")
            .then().assertThat().statusCode(409);
    }

    @Test
    public void testCreateAccount_Success() {
        AccountInputDTO newAccountToCreate = new AccountInputDTO();
        newAccountToCreate.setType(AccountType.MEMBER);
        newAccountToCreate.setActive(true);
        newAccountToCreate.setFirstName("Diego");
        newAccountToCreate.setLastName("T");
        newAccountToCreate.setUsername("Neo");
        newAccountToCreate.setPassword("password");
        RestAssured.given().port(port).body(newAccountToCreate)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/account/signup")
            .then().assertThat().statusCode(201);

        List<Account> accounts = accountRepository.findAll();
        assertEquals(3, accounts.size());

        AccountInputDTO adminAccount = new AccountInputDTO();
        adminAccount.setType(AccountType.ADMIN);
        adminAccount.setFirstName("Elodie");
        adminAccount.setLastName("K");
        adminAccount.setUsername("MISS");
        adminAccount.setPassword("password");
        RestAssured.given().port(port).body(adminAccount)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/account/signup")
            .then().assertThat().statusCode(201);

        accounts = accountRepository.findAll();
        assertEquals(4, accounts.size()); // 2 here + 1 for authentication

    }

    @Test
    public void testGetOnloanBooksByAccount_NoBooksOnLoan_Success() {
        BookDTO[] booksArray = RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> books = Arrays.asList(booksArray);
        assertEquals(0, books.size());
    }

    @Test
    public void testUnauthorizedMember() {
        RestAssured.given().port(port).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(403);
    }

    @Test
    public void testGetOnloanBooksByAccount_SomeBooksOnLoan_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);

        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Book book2 = createBook("BBB_123", author, "Independence Day");
        createItem("YYY", book2);

        BookDTO[] booksArray = RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> books = Arrays.asList(booksArray);
        assertEquals(1, books.size());

        RestAssured.given().port(port).pathParam("isbn", book2.getISBN()).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        booksArray = RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        books = Arrays.asList(booksArray);
        assertEquals(2, books.size());
    }

    @Test
    public void testGetAccountById_NotFound() {
        RestAssured.given().port(port).pathParam("id", "NOT_EXISTING")
            .expect()
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(403);
    }

    @Test
    public void testChangeRole() {
        List<Account> accounts = accountRepository.findAll();
        assertEquals(2, accounts.size());
        List<Account> admins = accounts.stream().filter(a -> a instanceof Librarian).collect(Collectors.toList());
        assertEquals(1, admins.size());
        RestAssured.given().port(port).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .when().post("/library-management/api/account/v1/accounts/{id}/role/change")
            .then().assertThat().statusCode(200);

        accounts = accountRepository.findAll();
        assertEquals(2, accounts.size());
        accounts.stream()
            .forEach(a -> assertTrue(a instanceof Librarian));

        RestAssured.given().port(port).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .when().post("/library-management/api/account/v1/accounts/{id}/role/change")
            .then().assertThat().statusCode(200);

        accounts = accountRepository.findAll();
        assertEquals(2, accounts.size());
        admins = accounts.stream().filter(a -> a instanceof Librarian).collect(Collectors.toList());
        assertEquals(1, admins.size());
    }

    @Test
    public void testExpirationToken() throws InterruptedException {
        RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(200)
            .extract().as(Member.class);

        Thread.sleep(3000);

        RestAssured.given().port(port).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .when().get("/library-management/api/account/v1/accounts/{id}")
            .then().assertThat().statusCode(403);
    }

}
