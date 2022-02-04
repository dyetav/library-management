package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.BookItem;
import com.training.librarymanagement.entities.Member;
import com.training.librarymanagement.enums.Availability;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.BookReservationRepository;
import com.training.librarymanagement.repositories.ItemRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CommonUtils {

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected LibraryRepository libraryRepository;

    @Autowired
    protected AuthorRepository authorRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected BookReservationRepository bookReservationRepository;

    protected void clearAllRepositories() {
        bookReservationRepository.deleteAll();
        itemRepository.deleteAll();
        libraryRepository.deleteAll();
        authorRepository.deleteAll();
    }

    protected Account createAccountMember(String username, String firstName, String lastName) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setUsername(username);
        member.setActive(true);
        return accountRepository.save(member);
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

}
