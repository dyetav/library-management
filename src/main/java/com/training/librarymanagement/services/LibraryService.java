package com.training.librarymanagement.services;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.dtos.AuthorDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.entities.dtos.BookItemsDTO;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import com.training.librarymanagement.enums.Availability;
import com.training.librarymanagement.exceptions.AuthorNotFoundException;
import com.training.librarymanagement.exceptions.BookConflictException;
import com.training.librarymanagement.exceptions.BookNotFoundException;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.BookReservationRepository;
import com.training.librarymanagement.repositories.ItemRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import com.training.librarymanagement.utils.LibraryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private static Logger LOG = LoggerFactory.getLogger(LibraryService.class);

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BookReservationRepository bookReservationRepository;

    public BookDTO getBooksByISBN(String isbn) throws BookNotFoundException {
        Optional<Book> book = libraryRepository.findById(isbn);
        BookDTO bookDTO = book.map(b -> LibraryMapper.toDTO(b)).orElseThrow(() -> new BookNotFoundException());
        return bookDTO;
    }

    public List<BookDTO> getBooks(Pageable pageable) {
        Page<Book> paginatedBooks = libraryRepository.findAll(pageable);
        return LibraryMapper.toDTOs(paginatedBooks.get().collect(Collectors.toList()));
    }

    public BookDTO createBook(BookInputDTO book) throws AuthorNotFoundException {
        Optional<Author> authorOpt = authorRepository.findById(book.getAuthorId());
        Author author = authorOpt.orElseThrow(() -> new AuthorNotFoundException());
        Book newBook = new Book();
        newBook.setAuthor(author);
        newBook.setTitle(book.getTitle());
        newBook.setISBN(book.getISBN());
        newBook.setSubjectCategory(book.getSubjectCategory());
        newBook.setRackNumber(book.getRackNumber());
        newBook.setPublicationDate(book.getPublicationDate());
        newBook = libraryRepository.save(newBook);
        return LibraryMapper.toDTO(newBook);
    }

    public void deleteBookByIsbn(String isbn) throws BookConflictException, BookNotFoundException {
        Optional<Book> optBook = libraryRepository.findById(isbn);
        if (optBook.isPresent()) {
            Book book = optBook.get();
            if (!CollectionUtils.isEmpty(book.getItems())) {
                throw new BookConflictException("Not possible to delete book: items not deleted");
            }
            libraryRepository.delete(book);
        } else {
            throw new BookNotFoundException();
        }
    }

    public void reserveBook(String isbn, String accountId, ReservationInputDTO reservationInput) throws BookNotFoundException, AccountNotFoundException, BookConflictException {
        Book book = libraryRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException());
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException());
        Set<BookItem> bookItems = book.getItems();
        boolean isAvailable = bookItems.stream().anyMatch(b -> b.getAvailablity().equals(Availability.AVAILABLE));
        if (isAvailable) {
            Set<BookItem> availableBookItems = bookItems.stream().filter(b -> b.getAvailablity().equals(Availability.AVAILABLE)).collect(Collectors.toSet());
            BookItem pickABook = availableBookItems.stream().findAny().get();
            pickABook.setAvailablity(Availability.ON_LOAN);
            pickABook = itemRepository.save(pickABook);
            BookReservation reservation = new BookReservation();
            reservation.setAccount(account);
            reservation.setBookItem(pickABook);

            if (reservationInput != null) {
                reservation.setStartBookingDate(Optional.ofNullable(reservationInput.getWishedStartDate()).orElse(new Date()));
                reservation.setEndBookingDate(
                    Optional
                    .ofNullable(reservationInput.getWishedEndDate())
                    .orElse(Date.from(reservation.getStartBookingDate().toInstant().plus(10, ChronoUnit.DAYS)))
                );
            } else {
                reservation.setStartBookingDate(new Date());
                reservation.setEndBookingDate(Date.from(reservation.getStartBookingDate().toInstant().plus(10, ChronoUnit.DAYS)));
            }

            reservation.setStartBookingDate(new Date());
            reservation.setEndBookingDate(Date.from(reservation.getStartBookingDate().toInstant().plus(10, ChronoUnit.DAYS)));
            bookReservationRepository.save(reservation);
        } else {
            LOG.error("Book withs ISBN {} not available for account {}", isbn, accountId);
            throw new BookConflictException("Book not available");
        }
    }

    public BookItemsDTO getAvailableBookItemsByISBN(String isbn) throws BookNotFoundException {
        Optional<Book> book = libraryRepository.findById(isbn);
        return book.map(b -> LibraryMapper.toBookItemsDTO(b)).orElseThrow(() -> new BookNotFoundException());
    }

}
