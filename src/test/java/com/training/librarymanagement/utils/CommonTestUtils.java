package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.Librarian;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.enums.Availability;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.BookReservationRepository;
import com.training.librarymanagement.repositories.FineRepository;
import com.training.librarymanagement.repositories.ItemRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonTestUtils {

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected LibraryRepository libraryRepository;

    @Autowired
    protected AuthorRepository authorRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected FineRepository fineRepository;

    @Autowired
    protected BookReservationRepository bookReservationRepository;

    protected void clearAllRepositories() {
        bookReservationRepository.deleteAll();
        itemRepository.deleteAll();
        libraryRepository.deleteAll();
        authorRepository.deleteAll();
        fineRepository.deleteAll();;
    }

    protected Account createAccount(String username, String firstName, String lastName, Boolean isActive) {
        Account newAccount = null;
        if (isActive == null) {
            newAccount = new Librarian();
        } else {
            newAccount = new Member();
            ((Member) newAccount).setActive(isActive);
        }
        newAccount.setFirstName(firstName);
        newAccount.setLastName(lastName);
        newAccount.setUsername(username);
        return accountRepository.save(newAccount);
    }

    protected Book createBook(String ISBN, Author author, String title) {
        Book book = new Book();
        book.setISBN(ISBN);
        book.setPublicationDate(new Date());
        book.setRackNumber("AAAA");
        book.setSubjectCategory("Science Fiction");
        book.setTitle(title);
        book.setAuthor(author);
        return libraryRepository.save(book);
    }

    protected BookItem createItem(String code, Book book) {
        return createItem(code, book, Availability.AVAILABLE);
    }

    protected BookItem createItem(String code, Book book, Availability availability) {
        BookItem item = new BookItem();
        item.setPrice(new BigDecimal("15.00"));
        item.setAvailablity(availability);
        item.setCode(code);
        item.setBook(book);
        item = itemRepository.save(item);

        Set<BookItem> items = new HashSet<>();
        items.add(item);
        book.setItems(items);
        libraryRepository.save(book);
        return item;
    }

    protected Author createAuthor(String firstName, String lastName) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return authorRepository.save(author);
    }

    //////////////////////////      ASSERTS      ///////////////////////////////////

    protected void checkNumberOfItems(Set<BookItem> bookItems, int howManyAvailable, int howManyOnLoan, int howManyReserved) {
        List<BookItem> reservedItems = bookItems.stream().filter(i -> i.getAvailablity().equals(Availability.RESERVED)).collect(Collectors.toList());
        assertEquals(howManyReserved, reservedItems.size());
        List<BookItem> onloanItems = bookItems.stream().filter(i -> i.getAvailablity().equals(Availability.ON_LOAN)).collect(Collectors.toList());
        assertEquals(howManyOnLoan, onloanItems.size());
        List<BookItem> availableItems = bookItems.stream().filter(i -> i.getAvailablity().equals(Availability.AVAILABLE)).collect(Collectors.toList());
        assertEquals(howManyAvailable, availableItems.size());
    }

}
