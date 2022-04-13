package com.training.librarymanagement.controllers;

import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.entities.dtos.BookItemsDTO;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import com.training.librarymanagement.entities.dtos.ReturnBookDTO;
import com.training.librarymanagement.exceptions.AccountNotFoundException;
import com.training.librarymanagement.exceptions.AuthorNotFoundException;
import com.training.librarymanagement.exceptions.BookConflictException;
import com.training.librarymanagement.exceptions.BookNotFoundException;
import com.training.librarymanagement.services.LibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/library-management/api/library")
@Api(value = "LibraryManagement")
public class LibraryController {

    private static Logger LOG = LoggerFactory.getLogger(LibraryController.class);

    @Autowired
    private LibraryService libraryService;

    @ApiOperation(value = "Ping", tags = {"library"})
    @GetMapping("/v1/ping")
    public String ping() {
        return "pong";
    }

    @ApiOperation(value = "Get a book by its ISBN", tags = {"library"})
    @GetMapping("/v1/books/{isbn}")
    public BookDTO getBooksByISBN(@PathVariable("isbn") String isbn) throws BookNotFoundException {
        LOG.info("Calling get books by ISBN with param isbn {}", isbn);
        BookDTO book = libraryService.getBooksByISBN(isbn);
        return book;
    }

    @ApiOperation(value = "Get a paginated list of all books", tags = {"library"})
    @GetMapping("/v1/books")
    public List<BookDTO> getBooks(Pageable pageable) {
        LOG.info("Calling get all books");
        List<BookDTO> book = libraryService.getBooks(pageable);
        return book;
    }

    // TODO: only ADMIN
    // TODO: ----------
    @ApiOperation(value = "Get the current owners of a book (who owns on-loan book items)", tags = {"library"})
    @GetMapping("/v1/books/{isbn}/accounts")
    public List<AccountDTO> getOwnersByBook(@PathVariable("isbn") String isbn)
        throws BookNotFoundException {

        LOG.info("Calling get all books");
        List<AccountDTO> owners = libraryService.getAccountsByBook(isbn);
        return owners;
    }

    @ApiOperation(value = "Create a book", tags = {"library"})
    @PostMapping("/v1/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO createBook(@RequestBody BookInputDTO input)
        throws AuthorNotFoundException {

        LOG.info("Callng create book");
        BookDTO createdBook = libraryService.createBook(input);
        return createdBook;
    }

    @ApiOperation(value = "Delete a book by its ISBN", tags = {"library"})
    @DeleteMapping("/v1/books/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookByISBN(@PathVariable("isbn") String isbn) throws BookConflictException, BookNotFoundException {
        LOG.info("Calling delete book by ISBN {}", isbn);
        libraryService.deleteBookByIsbn(isbn);
    }

    @ApiOperation(value = "Delete a book item by its ISBN and book item code", tags = {"library"})
    @DeleteMapping("/v1/books/{isbn}/items/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookByISBNAndItemCode(@PathVariable("isbn") String isbn, @PathVariable("code") String code) throws BookConflictException, BookNotFoundException {
        LOG.info("Calling delete book by ISBN {} and item code {}", isbn, code);
        libraryService.deleteBookItemByIsbnAndCode(isbn, code);
    }

    @ApiOperation(value = "Get the available items of a book by its ISBN", tags = {"library"})
    @GetMapping("/v1/books/{isbn}/available-items")
    public BookItemsDTO getAvailablBookItemsByISBN(@PathVariable String isbn)
        throws BookNotFoundException {

        LOG.info("Calling get available items by book and by ISBN {}", isbn);
        BookItemsDTO dto = libraryService.getAvailableBookItemsByISBN(isbn);
        return dto;
    }

    @ApiOperation(value = "Reserve a book by its ISBN", tags = {"library"})
    @PostMapping("/v1/books/{isbn}/account/{id}/reserve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void reserveBookByISBN(@PathVariable("isbn") String isbn, @PathVariable("id") String accountId, @RequestBody(required = false) ReservationInputDTO reservation)
        throws BookNotFoundException, BookConflictException, AccountNotFoundException {

        LOG.info("Calling reservation of a book by ISBN {} and for the account {}", isbn, accountId);
        libraryService.reserveBook(isbn, accountId, reservation);
    }

    @ApiOperation(value = "Checkout a book by its ISBN", tags = {"library"})
    @PostMapping("/v1/books/{isbn}/account/{id}/checkout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void checkoutBookByISBN(@PathVariable("isbn") String isbn, @PathVariable("id") String accountId)
        throws BookNotFoundException, AccountNotFoundException {

        LOG.info("Calling checkout of a book by ISBN {} and for the account {}", isbn, accountId);
        libraryService.checkout(isbn, accountId);
    }

    @ApiOperation(value = "Delete a reservation of a book by its ISBN", tags = {"library"})
    @DeleteMapping("/v1/books/{isbn}/account/{id}/reserve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteReservationBookByISBN(@PathVariable("isbn") String isbn, @PathVariable("id") String accountId)
        throws BookNotFoundException, AccountNotFoundException {

        LOG.info("Calling delete reservation of a book by ISBN {} and for the account {}", isbn, accountId);
        libraryService.deleteBookReservation(isbn, accountId);
    }

    @ApiOperation(value = "Return a book by its ISBN", tags = {"library"})
    @PostMapping("/v1/books/{isbn}/account/{id}/return")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void returnBookByISBN(@PathVariable("isbn") String isbn, @PathVariable("id") String accountId, @RequestBody(required = false) ReturnBookDTO returnInput)
        throws BookNotFoundException, AccountNotFoundException {

        LOG.info("Return Book with isbn {} for account {} with return {}", isbn, accountId, returnInput);
        libraryService.returnBook(isbn, accountId, returnInput);
    }

    @ApiOperation(value = "Renew a book by its ISBN", tags = {"library"})
    @PostMapping("/v1/books/{isbn}/account/{id}/renew")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void renewBookByISBN(@PathVariable("isbn") String isbn, @PathVariable("id") String accountId, @RequestBody(required = false) ReservationInputDTO reservation)
        throws BookNotFoundException, AccountNotFoundException, BookConflictException {


    }

}
