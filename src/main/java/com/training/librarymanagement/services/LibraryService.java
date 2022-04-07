package com.training.librarymanagement.services;

import com.training.librarymanagement.configuration.LibraryManagementConfiguration;
import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.BookReservation;
import com.training.librarymanagement.entities.dtos.AccountDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.entities.dtos.BookItemsDTO;
import com.training.librarymanagement.entities.dtos.ReservationInputDTO;
import com.training.librarymanagement.entities.dtos.ReturnBookDTO;
import com.training.librarymanagement.enums.Availability;
import com.training.librarymanagement.exceptions.AccountNotFoundException;
import com.training.librarymanagement.exceptions.AuthorNotFoundException;
import com.training.librarymanagement.exceptions.BookConflictException;
import com.training.librarymanagement.exceptions.BookNotFoundException;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.BookReservationRepository;
import com.training.librarymanagement.repositories.ItemRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import com.training.librarymanagement.utils.AccountMapper;
import com.training.librarymanagement.utils.LibraryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private static Logger LOG = LoggerFactory.getLogger(LibraryService.class);

    @Autowired
    private LibraryManagementConfiguration conf;

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

    @Autowired
    private FineService fineService;

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
        Author author = authorRepository.findById(book.getAuthorId()).orElseThrow(AuthorNotFoundException::new);
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
        Book book = libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);
        if (!CollectionUtils.isEmpty(book.getItems())) {
            throw new BookConflictException("Not possible to delete book: items not deleted");
        }
        libraryRepository.delete(book);
    }

    public void reserveBook(String isbn, String accountId, ReservationInputDTO reservationInput) throws BookNotFoundException, AccountNotFoundException, BookConflictException {
        Book book = libraryRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException());
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException());
        Set<BookReservation> accountBookReservation = account.getBookReservation();
        Set<BookItem> reservedBookItems = accountBookReservation.stream().map(BookReservation::getBookItem).collect(Collectors.toSet());
        Set<BookItem> bookItems = book.getItems();

        boolean isBookAlreadyReserved = reservedBookItems.stream().anyMatch(b -> b.getBook().getISBN().equalsIgnoreCase(isbn));
        if (isBookAlreadyReserved) {
            LOG.error("Book withs ISBN {} already reserved by account {}", isbn, accountId);
            throw new BookConflictException("Book already reserved");
        }

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
                reservation.setStartBookingDate(Optional.ofNullable(reservationInput.getWishedStartDate()).orElse(Date.from(Instant.now())));
                reservation.setEndBookingDate(
                    Optional
                        .ofNullable(reservationInput.getWishedEndDate())
                        .orElse(Date.from(reservation.getStartBookingDate().toInstant().plus(10, ChronoUnit.DAYS)))
                );
            } else {
                reservation.setStartBookingDate(Date.from(Instant.now()));
                reservation.setEndBookingDate(null);
            }

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

    public List<AccountDTO> getAccountsByBook(String isbn) throws BookNotFoundException {
        Book book = libraryRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException());
        Set<BookItem> bookItems = book.getItems();
        List<BookItem> onLoanItems = bookItems.stream().filter(b -> b.getAvailablity().equals(Availability.ON_LOAN)).collect(Collectors.toList());
        List<String> onLoanItemIds = onLoanItems.stream().map(BookItem::getCode).collect(Collectors.toList());
        List<Account> owners = bookReservationRepository.findOwnersByBookItemIds(onLoanItemIds);
        return AccountMapper.toDTOs(owners);
    }

    public void returnBook(String isbn, String accountId, ReturnBookDTO returnInput) throws BookNotFoundException, AccountNotFoundException {
        libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);
        accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);
        LocalDateTime returnDate = returnInput.getReturnDate();
        Set<BookReservation> reservationsByAccount = bookReservationRepository.findReservationByAccountId(accountId);
        BookReservation reservationTarget = reservationsByAccount.stream()
            .filter(br -> br.getBookItem().getBook().getISBN().equalsIgnoreCase(isbn))
            .findFirst().get();
        if (returnDate == null) {
            reservationTarget.setEndBookingDate(new Date());
        } else {
            reservationTarget.setEndBookingDate(Date.from(returnDate.atZone(ZoneId.systemDefault()).toInstant()));
        }

        // make the book item available
        reservationTarget.getBookItem().setAvailablity(Availability.AVAILABLE);
        reservationTarget = bookReservationRepository.save(reservationTarget);

        // apply fine if any
        boolean outOfTime = isReservationOutOfTime(reservationTarget);
        if (outOfTime) {
            fineService.createFine(isbn, accountId);
        }
    }

    public void deleteBookItemByIsbnAndCode(String isbn, String code) throws BookNotFoundException, BookConflictException {
        Book book = libraryRepository.findById(isbn).orElseThrow(BookNotFoundException::new);
        Set<BookItem> bookItems = book.getItems();
        BookItem bookItemToDelete = bookItems.stream().filter(bi -> bi.getCode().equals(code)).findFirst().orElseThrow(BookNotFoundException::new);
        if (bookItemToDelete.getBookReservations().isEmpty()) {
            bookItems.remove(bookItemToDelete);
            libraryRepository.save(book);
            itemRepository.delete(bookItemToDelete);
        } else {
            throw new BookConflictException("Book Item to delete has got a reservation");
        }
    }

    public void deleteBookReservation(String isbn, String accountId) {

    }

    private boolean isReservationOutOfTime(BookReservation reservationTarget) {
        LocalDateTime startBookingDateTime = reservationTarget.getStartBookingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endBookingDateTime = reservationTarget.getEndBookingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long reservedDays = ChronoUnit.DAYS.between(startBookingDateTime, endBookingDateTime);
        return reservedDays > conf.getReturnDays();
    }

}
