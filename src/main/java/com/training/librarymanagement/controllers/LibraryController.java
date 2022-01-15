package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.exceptions.AuthorNotFoundException;
import com.training.librarymanagement.exceptions.BookConflictException;
import com.training.librarymanagement.exceptions.BookNotFoundException;
import com.training.librarymanagement.services.LibraryService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/library-management/api/library")
@Api(value = "LibraryManagement")
public class LibraryController {

    private static Logger LOG = LoggerFactory.getLogger(LibraryController.class);

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/v1/books/{isbn}")
    public BookDTO getBooksByISBN(@PathVariable("isbn") String isbn) throws BookNotFoundException {
        LOG.info("Calling get books by ISBN with param isbn {}", isbn);
        BookDTO book = libraryService.getBooksByISBN(isbn);
        return book;
    }

    @GetMapping("/v1/books")
    public List<BookDTO> getBooks(Pageable pageable) {
        LOG.info("Calling get all books");
        List<BookDTO> book = libraryService.getBooks(pageable);
        return book;
    }

    @PostMapping("/v1/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody BookInputDTO input) throws AuthorNotFoundException {
        LOG.info("Callng create book");
        BookDTO createdBook = libraryService.createBook(input);
        return createdBook;
    }

    @DeleteMapping("/v1/books/{ISBN}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookByISBN(@PathVariable("ISBN") String isbn) throws BookConflictException, BookNotFoundException {
        LOG.info("Calling delete book by ISBN {}", isbn);
        libraryService.deleteBookByIsbn(isbn);
    }

}
