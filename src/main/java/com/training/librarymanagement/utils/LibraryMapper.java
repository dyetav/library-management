package com.training.librarymanagement.utils;

import com.training.librarymanagement.entities.Author;
import com.training.librarymanagement.entities.Book;
import com.training.librarymanagement.entities.dtos.AuthorDTO;
import com.training.librarymanagement.entities.dtos.BookDTO;
import com.training.librarymanagement.entities.dtos.BookItemsDTO;
import com.training.librarymanagement.enums.Availability;

import java.util.List;
import java.util.stream.Collectors;

public class LibraryMapper {

    public LibraryMapper() {
        // NOTHING
    }

    public static BookItemsDTO toBookItemsDTO(Book b) {
        BookItemsDTO output = new BookItemsDTO();
        output.setAvailableItems((int) b.getItems().stream().filter(i -> i.getAvailablity().equals(Availability.AVAILABLE)).count());
        return output;
    }

    public static List<BookDTO> toDTOs(List<Book> books) {
        List<BookDTO> bookDTOs = books.stream().map(b -> toDTO(b)).collect(Collectors.toList());
        return bookDTOs;
    }

    public static BookDTO toDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setISBN(book.getISBN());
        bookDTO.setRackNumber(book.getRackNumber());
        bookDTO.setPublicationDate(book.getPublicationDate());
        bookDTO.setSubjectCategory(book.getSubjectCategory());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(toDTO(book.getAuthor()));
        return bookDTO;
    }

    public static AuthorDTO toDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setFirstName(author.getFirstName());
        authorDTO.setLastName(author.getLastName());
        return authorDTO;
    }
}
