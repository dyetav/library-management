package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.services.LibraryService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/library-management/api/library")
@Api(value = "LibraryManagement")
public class LibraryController {

    private static Logger LOG = LoggerFactory.getLogger(LibraryController.class);

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/v1/books/{isbn}")
    public BookDTO getBooksByISBN(@PathVariable("isbn") String isbn) {
        LOG.info("Calling get books by ISBN with param isbn {}", isbn);
        BookDTO book = libraryService.getBooksByISBN(isbn);
        return book;
    }

}
