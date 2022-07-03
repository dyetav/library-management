package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.Fine;
import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.entities.dtos.BookItemsDTO;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import com.training.librarymanagement.entities.dtos.ReturnBookDTO;
import com.training.librarymanagement.enums.Availability;
import com.training.librarymanagement.enums.FineStatus;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(value = SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryControllerIT extends CommonTestUtils {

    @LocalServerPort
    private int port;

    private String tokenMember;
    private String tokenAdmin;
    private Account member;
    private Account admin;

    @BeforeEach
    public void setup() {
        super.clearAllRepositories();
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
    public void tearDown() {
        super.clearAllRepositories();
    }

    @Test
    public void testGetBooksByIsbn_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("ABC_123", author, "Matrix");
        BookDTO testedBook = RestAssured.given().port(port).pathParam("isbn", book.getISBN())
            .header("Authorization", "Bearer " + tokenMember)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books/{isbn}")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO.class);

        assertEquals("Matrix", testedBook.getTitle());
        assertEquals("ABC_123", testedBook.getISBN());
        assertEquals("Diego", testedBook.getAuthor().getFirstName());
    }

    @Test
    public void testGetBooksByIsbn_BookNotExisting() {
        RestAssured.given().port(port).pathParam("isbn", "NOT_EXISTING")
            .header("Authorization", "Bearer " + tokenMember)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books/{isbn}")
            .then().assertThat().statusCode(404);

    }

    @Test
    public void testGetBooks_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        createBook("DEF_456", author, "Matrix1");
        createBook("ABC_123", author, "Matrix2");
        BookDTO[] books = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(2, bookList.size());
        List<String> isbns = bookList.stream().map(BookDTO::getISBN).collect(Collectors.toList());
        assertTrue(isbns.contains("DEF_456"));
        assertTrue(isbns.contains("ABC_123"));
    }

    @Test
    public void testGetBooks_FilteringByTitle_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        createBook("DEF_456", author, "Matrix1");
        createBook("ABC_123", author, "Matrix2");
        BookDTO[] books = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .queryParam("title", "Matrix1")
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(1, bookList.size());
        List<String> isbns = bookList.stream().map(BookDTO::getISBN).collect(Collectors.toList());
        assertTrue(isbns.contains("DEF_456"));
    }

    @Test
    public void testGetBooks_FilteringByTitleAndSubject_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book1 = createBook("DEF_456", author, "Matrix1");
        Book book2 = createBook("ABC_123", author, "Matrix2");
        book2.setSubjectCategory("Documentary");
        libraryRepository.save(book2);
        BookDTO[] books = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .queryParam("title", "Matrix1")
            .queryParam("category", "Documentary")
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(0, bookList.size());
    }

    @Test
    public void testGetBooks_FilteringAuthor_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Author author2 = createAuthor("Elodie", "Klauder");
        Book book1 = createBook("DEF_456", author, "Matrix1");
        Book book2 = createBook("ABC_123", author2, "Matrix2");
        BookDTO[] books = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .queryParam("author", "Klauder")
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(1, bookList.size());
    }

    @Test
    public void testGetManyBooks_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        for (int i = 0; i < 27; i++) {
            createBook(UUID.randomUUID().toString(), author, "Matrix" + i);
        }

        BookDTO[] books = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books?page=0&size=25")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(25, bookList.size());
    }

    @Test
    public void testGetBooks_Success_EmptyList() {
        BookDTO[] books = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(0, bookList.size());
    }

    @Test
    public void testCreateBook_ExistingAuthor_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        BookInputDTO book = new BookInputDTO();
        book.setAuthorId(author.getId());
        book.setTitle("Matrix");
        book.setISBN("AAA_123");
        book.setPublicationDate(new Date());
        book.setRackNumber("678");
        book.setSubjectCategory("Science Fiction");

        BookDTO response = RestAssured.given().port(port).body(book)
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect().contentType(ContentType.JSON)
            .when().post("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(201)
            .extract().as(BookDTO.class);

        assertEquals("Matrix", response.getTitle());
        assertEquals("Diego", response.getAuthor().getFirstName());
        assertEquals("Tavolaro", response.getAuthor().getLastName());
        assertEquals("AAA_123", response.getISBN());
    }

    @Test
    public void testCreateBook_ExistingAuthor_UnauthorizedRequestFromMember() {
        Author author = createAuthor("Diego", "Tavolaro");
        BookInputDTO book = new BookInputDTO();
        book.setAuthorId(author.getId());
        book.setTitle("Matrix");
        book.setISBN("AAA_123");
        book.setPublicationDate(new Date());
        book.setRackNumber("678");
        book.setSubjectCategory("Science Fiction");

        RestAssured.given().port(port).body(book)
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect().contentType(ContentType.JSON)
            .when().post("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(403);

    }

    @Test
    public void testCreateBook_NotExistingAuthor_NotFound() {
        BookInputDTO book = new BookInputDTO();
        book.setAuthorId("NOT_EXISTING");
        book.setTitle("Matrix");
        book.setISBN("AAA_123");
        book.setPublicationDate(new Date());
        book.setRackNumber("678");
        book.setSubjectCategory("Science Fiction");

        RestAssured.given().port(port).body(book)
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect().contentType(ContentType.JSON)
            .when().post("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(404);

    }

    @Test
    public void testCreateBook_NotUniqueTitle() {
        Author author = createAuthor("Diego", "Tavolaro");
        BookInputDTO book = new BookInputDTO();
        book.setAuthorId(author.getId());
        book.setTitle("Matrix");
        book.setISBN("AAA_123");
        book.setPublicationDate(new Date());
        book.setRackNumber("678");
        book.setSubjectCategory("Science Fiction");

        RestAssured.given().port(port).body(book)
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect().contentType(ContentType.JSON)
            .when().post("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(201);

        BookInputDTO secondBook = new BookInputDTO();
        secondBook.setAuthorId(author.getId());
        secondBook.setTitle("Matrix");
        secondBook.setISBN("AAA_124");
        secondBook.setPublicationDate(new Date());
        secondBook.setRackNumber("205");
        secondBook.setSubjectCategory("Drama");

        RestAssured.given().port(port).body(secondBook)
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect().contentType(ContentType.JSON)
            .when().post("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(409);

    }

    @Test
    public void testDeleteBookByISBN() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");

        RestAssured.given().port(port).pathParam("isbn", book.getISBN())
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}")
            .then().assertThat().statusCode(204);
    }

    @Test
    public void testDeleteBookByISBN_Failure_BookHasReservations() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);

        RestAssured.given().port(port).pathParam("isbn", book.getISBN())
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}")
            .then().assertThat().statusCode(409);
    }

    @Test
    public void testReserveBookByISBN_AvailableItem_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(2, bookItems.size());

        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());
        assertEquals(new BigDecimal("15.00"), reservations.stream().findFirst().get().getBookItem().getPrice());

        BookReservation reservation = reservations.stream().findFirst().get();

        assertEquals(member.getId(), reservation.getAccount().getId());
        assertEquals(reservation.getBookItem().getCode(), reservation.getBookItem().getCode());

    }

    @Test
    public void testReserveBookByISBN_AvailableItem_ReserveAnAlreadyReservedBook_Error() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(409);

    }

    @Test
    public void testReserveBookByISBN_NotAvailableItem_ErrorMessage() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        assertEquals(2, initialBookItems.size());

        Account member2 = createAccount("virkla", "password", "Virgile", "Klauder", true);
        Account member3 = createAccount("elokla", "password", "Elodie", "Klauder", true);

        // booking two items, the third reservation fails
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member.getId())
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member2.getId())
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member3.getId())
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(409);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(2, bookItems.size());
        Set<BookReservation> resMember1 = bookReservationRepository.findByAccount(member);
        Set<BookReservation> resMember2 = bookReservationRepository.findByAccount(member2);
        assertEquals(1, resMember1.size());
        assertEquals(1, resMember2.size());
        assertTrue(resMember1.stream().findFirst().get().getAvailability().equals(Availability.ON_LOAN));
        assertTrue(resMember2.stream().findFirst().get().getAvailability().equals(Availability.ON_LOAN));
    }

    @Test
    public void testReserveBookByISBN_OnlyOneItem_DifferentMembers_ReservationNotOverlapping() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        assertEquals(1, initialBookItems.size());

        Account member2 = createAccount("elokla", "password", "Elodie", "Klauder", true);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("elokla", "password");
        String tokenMember2 = RestAssured.given().port(port)
            .body(authenticationRequest)
            .expect()
            .when().post("/library-management/signin")
            .then().assertThat().statusCode(200)
            .extract().header("Authorization");
        ReservationInputDTO reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(11L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(16L, ChronoUnit.DAYS))
        );
        // booking two items, the third reservation fails
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member.getId())
            .body(reservationInputDTO)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember2)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member2.getId())
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(1, bookItems.size());
        Set<BookReservation> resMember1 = bookReservationRepository.findByAccount(member);
        Set<BookReservation> resMember2 = bookReservationRepository.findByAccount(member2);
        assertEquals(1, resMember1.size());
        assertEquals(1, resMember2.size());
        assertTrue(resMember1.stream().findFirst().get().getAvailability().equals(Availability.RESERVED));
        assertTrue(resMember2.stream().findFirst().get().getAvailability().equals(Availability.ON_LOAN));
        assertEquals(2, bookReservationRepository.findAll().size());
    }

    @Test
    public void testReserveBookByISBN_OnlyOneItem_DifferentMembers_ReservationOverlapping_SecondMemberError() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        assertEquals(1, initialBookItems.size());

        Account member2 = createAccount("elokla", "password", "Elodie", "Klauder", true);
        ReservationInputDTO reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(5L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(16L, ChronoUnit.DAYS))
        );
        // booking two items, the third reservation fails
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member.getId())
            .body(reservationInputDTO)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .pathParam("id", member2.getId())
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(409);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(1, bookItems.size());
        Set<BookReservation> resMember1 = bookReservationRepository.findByAccount(member);
        Set<BookReservation> resMember2 = bookReservationRepository.findByAccount(member2);
        assertEquals(1, resMember1.size());
        assertEquals(0, resMember2.size());
        assertTrue(resMember1.stream().findFirst().get().getAvailability().equals(Availability.RESERVED));
        assertEquals(1, bookReservationRepository.findAll().size());
    }

    @Test
    public void testCheckout_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        ReservationInputDTO reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(5L, ChronoUnit.DAYS))
        );
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(reservationInputDTO)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(2, bookItems.size());
        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.RESERVED)).count());

        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(reservationInputDTO)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/checkout")
            .then().assertThat().statusCode(202);

        reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        reservedBook = reservedBookOpt.get();
        bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(2, bookItems.size());
        reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());

    }

    @Test
    public void testGetAvailableBookItemsPerBook_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("DEF_456", author, "Matrix1");
        createItem("XXX", book);
        createItem("YYY", book);
        BookItem onLoanBookItem = createItem("ZZZ", book);
        createReservation(onLoanBookItem, Date.from(Instant.now()), Date.from(Instant.now().plus(10L, ChronoUnit.DAYS)), Availability.ON_LOAN);
        BookItemsDTO output = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books/{isbn}/available-items")
            .then().assertThat().statusCode(200)
            .extract().as(BookItemsDTO.class);

        assertEquals(3, itemRepository.findByBook(book).size());
        assertEquals(2, output.getAvailableItems());
    }

    @Test
    public void testGetAvailableBookItemsPerBook_NoAvailableItems_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("DEF_456", author, "Matrix1");
        BookItem b1 = createItem("XXX", book);
        createReservation(b1, Date.from(Instant.now()), Date.from(Instant.now().plus(10L, ChronoUnit.DAYS)), Availability.ON_LOAN);
        BookItem b2 = createItem("YYY", book);
        createReservation(b2, Date.from(Instant.now()), Date.from(Instant.now().plus(10L, ChronoUnit.DAYS)), Availability.ON_LOAN);
        BookItem b3 = createItem("ZZZ", book);
        createReservation(b3, Date.from(Instant.now()), Date.from(Instant.now().plus(10L, ChronoUnit.DAYS)), Availability.ON_LOAN);
        BookItemsDTO output = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN())
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books/{isbn}/available-items")
            .then().assertThat().statusCode(200)
            .extract().as(BookItemsDTO.class);

        assertEquals(3, itemRepository.findByBook(book).size());
        assertEquals(0, output.getAvailableItems());
    }

    @Test
    public void testGetOwnersByBook_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        createItem("ZZZ", book);
        Account member2 = createAccount("elodie", "password", "Elodie", "Tavolaro", true);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("elodie", "password");
        String tokenMember2 = RestAssured.given().port(port)
            .body(authenticationRequest)
            .expect()
            .when().post("/library-management/signin")
            .then().assertThat().statusCode(200)
            .extract().header("Authorization");
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", admin.getId())
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member2.getId())
            .header("Authorization", "Bearer " + tokenMember2)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        AccountDTO[] accounts = RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN())
            .contentType(ContentType.JSON).expect()
            .when().get("/library-management/api/library/v1/books/{isbn}/accounts")
            .then().assertThat().statusCode(200)
            .extract().response().as(AccountDTO[].class);

        assertNotNull(accounts);
        assertEquals(3, accounts.length);
        List<AccountDTO> accountList = Arrays.asList(accounts);
        List<String> usernames = accountList.stream().map(AccountDTO::getUsername).collect(Collectors.toList());
        assertTrue(usernames.contains("dietav"));
        assertTrue(usernames.contains("giutav"));
        assertTrue(usernames.contains("elodie"));

        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(3, reservations.size());
        assertEquals(3, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());
        usernames = reservations.stream().map(br -> br.getAccount().getUsername()).collect(Collectors.toList());
        assertTrue(usernames.contains("dietav"));
        assertTrue(usernames.contains("giutav"));
        assertTrue(usernames.contains("elodie"));
    }

    @Test
    public void testReturnBook_WithoutFineApplication_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        createItem("ZZZ", book);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        assertEquals(3, initialBookItems.size());
        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());

        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(4L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(202);

        initialBookItems = itemRepository.findByBook(book);
        assertEquals(3, initialBookItems.size());
        reservations = bookReservationRepository.findAll();
        assertEquals(0, reservations.size());

        List<Fine> fines = fineRepository.findAll();
        assertEquals(0, fines.size());
    }

    @Test
    public void testReturnBook_Fail_NoReturnForReservedBook() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        createItem("ZZZ", book);
        ReservationInputDTO reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(5L, ChronoUnit.DAYS))
        );
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(reservationInputDTO)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        assertEquals(3, initialBookItems.size());
        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.RESERVED)).count());

        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(4L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(409);

        initialBookItems = itemRepository.findByBook(book);
        assertEquals(3, initialBookItems.size());
        reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.RESERVED)).count());

        List<Fine> fines = fineRepository.findAll();
        assertEquals(0, fines.size());
    }

    @Test
    public void testReturnBook_Fail_NoBookReservation() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        createItem("ZZZ", book);

        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        assertEquals(3, initialBookItems.size());
        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(0, reservations.size());

        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(4L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(404);

        initialBookItems = itemRepository.findByBook(book);
        assertEquals(3, initialBookItems.size());
        reservations = bookReservationRepository.findAll();
        assertEquals(0, reservations.size());

        List<Fine> fines = fineRepository.findAll();
        assertEquals(0, fines.size());
    }

    @Test
    public void testReturnBook_WithFineApplication_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        createItem("ZZZ", book);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Set<BookItem> initialBookItems = itemRepository.findByBook(book);
        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());

        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(12L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(202);

        initialBookItems = itemRepository.findByBook(book);
        reservations = bookReservationRepository.findAll();
        assertEquals(0, reservations.size());

        List<Fine> fines = fineRepository.findAll();
        assertEquals(1, fines.size());
        Fine fine = fines.get(0);
        assertEquals(FineStatus.PENDING, fine.getFineStatus());
        assertEquals(new BigDecimal("8.00"), fine.getPrice());
        Account accountWithFine = accountRepository.getById(fine.getAccount().getId());
        assertEquals(accountWithFine.getId(), member.getId());
    }

    @Test
    public void testReturnBook_BookNotFound() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(4L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", "NOT_EXISTING_BOOK").pathParam("id", member.getId())
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(404);

    }

    @Test
    public void testReturnBook_AccountNotFound() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(4L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", "NOT_EXISTING_ACCOUNT")
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(404);

    }

    @Test
    public void testReturnBook_BookReservationNotFound() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        ReturnBookDTO returnBook = new ReturnBookDTO();
        returnBook.setReturnDate(LocalDateTime.now().plusDays(4L));
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenAdmin)
            .pathParam("isbn", book.getISBN()).pathParam("id", "NOT_EXISTING_ACCOUNT")
            .body(returnBook)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/return")
            .then().assertThat().statusCode(404);
    }

    @Test
    public void testDeleteBookItem_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        createItem("YYY", book);
        createItem("ZZZ", book);

        assertEquals(3, itemRepository.findByBook(book).size());

        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("code", "YYY")
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/items/{code}")
            .then().assertThat().statusCode(204);

        assertEquals(2, itemRepository.findByBook(book).size());

    }

    @Test
    public void testDeleteBookItem_BookItemNotExisting() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);

        assertEquals(1, itemRepository.findByBook(book).size());

        RestAssured.given().port(port).pathParam("isbn", "NOT_EXISTING_ISBN_BOOK").pathParam("code", "XXX")
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/items/{code}")
            .then().assertThat().statusCode(404);

        assertEquals(1, itemRepository.findByBook(book).size());

    }

    @Test
    public void testDeleteBookItem_BookItemWithReservation_Fail() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);

        assertEquals(1, itemRepository.findByBook(book).size());

        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .header("Authorization", "Bearer " + tokenMember)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        RestAssured.given().port(port).pathParam("isbn", book.getISBN()).pathParam("code", "XXX")
            .header("Authorization", "Bearer " + tokenAdmin)
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/items/{code}")
            .then().assertThat().statusCode(409);

        assertEquals(1, itemRepository.findByBook(book).size());

    }

    @Test
    public void testDeleteReservationByBookAndAccount_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        ReservationInputDTO reservationInputDTO = new ReservationInputDTO(
            Date.from(Instant.now().plus(1L, ChronoUnit.DAYS)),
            Date.from(Instant.now().plus(5L, ChronoUnit.DAYS))
        );
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .body(reservationInputDTO)
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(1, bookItems.size());
        Set<BookReservation> reservations = bookReservationRepository.findByBookItem(bookItems.stream().findFirst().get());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.RESERVED)).count());

        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        reservedBook = reservedBookOpt.get();
        bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(1, bookItems.size());
        reservations = bookReservationRepository.findByBookItem(bookItems.stream().findFirst().get());
        assertEquals(0, reservations.size());

    }

    @Test
    public void testDeleteReservation_Fail_ReservationOnLoan() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        createItem("XXX", book);
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .contentType(ContentType.JSON).expect()
            .when().post("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(202);

        Optional<Book> reservedBookOpt = libraryRepository.findById(book.getISBN());
        assertTrue(reservedBookOpt.isPresent());
        Book reservedBook = reservedBookOpt.get();
        Set<BookItem> bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(1, bookItems.size());
        List<BookReservation> reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());

        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN()).pathParam("id", member.getId())
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(409);

        bookItems = itemRepository.findByBook(reservedBook);
        assertEquals(1, bookItems.size());
        reservations = bookReservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.stream().filter(br -> br.getAvailability().equals(Availability.ON_LOAN)).count());
    }

    @Test
    public void testDeleteReservationByBookAndAccount_Fail_AccountNotExisting() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", book.getISBN()).pathParam("id", "NOT_EXISTING_ACCOUNT")
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(404);
    }

    @Test
    public void testDeleteReservationByBookAndAccount_Fail_BookNotExisting() {
        RestAssured.given().port(port)
            .header("Authorization", "Bearer " + tokenMember)
            .pathParam("isbn", "NOT_EXISTING_BOOK").pathParam("id", member.getId())
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}/account/{id}/reserve")
            .then().assertThat().statusCode(404);
    }

}

