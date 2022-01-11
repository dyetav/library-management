package com.training.librarymanagement.services;

import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.dtos.AuthorDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.repositories.LibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private static Logger LOG = LoggerFactory.getLogger(LibraryService.class);

    @Autowired
    private LibraryRepository libraryRepository;

    public BookDTO getBooksByISBN(String isbn) {
        Optional<Book> book = libraryRepository.findById(isbn);
        BookDTO bookDTO = null;
        if (book.isPresent()) {
            bookDTO = toDTO(book.get());
        }
        return bookDTO;
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
