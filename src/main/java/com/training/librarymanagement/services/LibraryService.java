package com.training.librarymanagement.services;

import com.training.librarymanagement.entities.Account;
import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.dtos.AuthorDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookInputDTO;
import com.training.librarymanagement.exceptions.AuthorNotFoundException;
import com.training.librarymanagement.exceptions.BookConflictException;
import com.training.librarymanagement.exceptions.BookNotFoundException;
import com.training.librarymanagement.repositories.AccountRepository;
import com.training.librarymanagement.repositories.AuthorRepository;
import com.training.librarymanagement.repositories.ItemRepository;
import com.training.librarymanagement.repositories.LibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public BookDTO getBooksByISBN(String isbn) throws BookNotFoundException {
        Optional<Book> book = libraryRepository.findById(isbn);
        BookDTO bookDTO = book.map(b -> toDTO(b)).orElseThrow(() -> new BookNotFoundException());
        return bookDTO;
    }

    public List<BookDTO> getBooks(Pageable pageable) {
        Page<Book> paginatedBooks = libraryRepository.findAll(pageable);
        return toDTOs(paginatedBooks.get().collect(Collectors.toList()));
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
        return toDTO(newBook);
    }

    public void deleteBookByIsbn(String isbn) throws BookConflictException, BookNotFoundException {
        Optional<Book> optBook = libraryRepository.findById(isbn);
        if (optBook.isPresent()) {
            Book book = optBook.get();
            if (!CollectionUtils.isEmpty(book.getItems())) {
                throw new BookConflictException();
            }
            libraryRepository.delete(book);
        } else {
            throw new BookNotFoundException();
        }
    }

    public void reserveBook(String isbn, String accountId) throws BookNotFoundException, AccountNotFoundException {
        Book book = libraryRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException());
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException());


    }

    private List<BookDTO> toDTOs(List<Book> books) {
        List<BookDTO> bookDTOs = books.stream().map(b -> toDTO(b)).collect(Collectors.toList());
        return bookDTOs;
    }

    private BookDTO toDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setISBN(book.getISBN());
        bookDTO.setRackNumber(book.getRackNumber());
        bookDTO.setPublicationDate(book.getPublicationDate());
        bookDTO.setSubjectCategory(book.getSubjectCategory());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(toDTO(book.getAuthor()));
        return bookDTO;
    }

    private AuthorDTO toDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setFirstName(author.getFirstName());
        authorDTO.setLastName(author.getLastName());
        return authorDTO;
    }

}
