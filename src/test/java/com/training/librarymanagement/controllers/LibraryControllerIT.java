package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Book book;
    private Author author;

    @BeforeEach
    public void setup() {
        author = new Author();
        author.setFirstName("Diego");
        author.setLastName("Tavolaro");
        author = authorRepository.save(author);

        book = new Book();
        book.setISBN("ABC_123");
        book.setPublicationDate(new Date());
        book.setRackNumber("AAAA");
        book.setSubjectCategory("Science Fiction");
        book.setTitle("Matrix");
        book.setAuthor(author);
        book = libraryRepository.save(book);
    }

    @Test
    public void testGetBooksByIsbn_Success() {
        Book testedBook = RestAssured.given().port(port).pathParam("isbn", book.getISBN())
            .expect().contentType(ContentType.JSON)
            .when().get("/library-management/api/library/v1/books/{isbn}")
            .then().assertThat().statusCode(200)
            .extract().as(Book.class);

        assertEquals("Matrix", testedBook.getTitle());
        assertEquals("ABC_123", testedBook.getISBN());
        assertEquals("Diego", testedBook.getAuthor().getFirstName());
    }
}
