package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.enums.Availability;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.ItemRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(value = SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ItemRepository itemRepository;

    @AfterEach
    public void tearDown() {
        itemRepository.deleteAll();
        libraryRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void testGetBooksByIsbn_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("ABC_123", author, "Matrix");
        BookDTO testedBook = RestAssured.given().port(port).pathParam("isbn", book.getISBN())
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
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(200)
            .extract().as(BookDTO[].class);

        List<BookDTO> bookList = Arrays.asList(books);
        assertEquals(2, bookList.size());
    }

    @Test
    public void testGetManyBooks_Success() {
        Author author = createAuthor("Diego", "Tavolaro");
        for (int i = 0; i < 27; i++) {
            createBook(UUID.randomUUID().toString(), author, "Matrix" + i);
        }

        BookDTO[] books = RestAssured.given().port(port)
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
    public void testCreateBook_NotExistingAuthor_NotFound() {
        BookInputDTO book = new BookInputDTO();
        book.setAuthorId("NOT_EXISTING");
        book.setTitle("Matrix");
        book.setISBN("AAA_123");
        book.setPublicationDate(new Date());
        book.setRackNumber("678");
        book.setSubjectCategory("Science Fiction");

        RestAssured.given().port(port).body(book)
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
            .contentType(ContentType.JSON).expect().contentType(ContentType.JSON)
            .when().post("/library-management/api/library/v1/books")
            .then().assertThat().statusCode(409);

    }

    @Test
    public void testDeleteBookByISBN() {
        Author author = createAuthor("Diego", "Tavolaro");
        Book book = createBook("AAA_123", author, "Matrix");

        RestAssured.given().port(port).pathParam("isbn", book.getISBN())
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
            .contentType(ContentType.JSON).expect()
            .when().delete("/library-management/api/library/v1/books/{isbn}")
            .then().assertThat().statusCode(409);
    }

    private Book createBook(String ISBN, Author author, String title) {
        Book book = new Book();
        book.setISBN(ISBN);
        book.setPublicationDate(new Date());
        book.setRackNumber("AAAA");
        book.setSubjectCategory("Science Fiction");
        book.setTitle(title);
        book.setAuthor(author);
        return libraryRepository.save(book);
    }

    private BookItem createItem(String code, Book book) {
        BookItem item = new BookItem();
        item.setPrice(new BigDecimal("15.00"));
        item.setAvailablity(Availability.AVAILABLE);
        item.setCode(code);
        item.setBook(book);
        item = itemRepository.save(item);

        Set<BookItem> items = new HashSet<>();
        items.add(item);
        book.setItems(items);
        book = libraryRepository.save(book);
        return item;
    }

    private Author createAuthor(String firstName, String lastName) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return authorRepository.save(author);
    }
}

