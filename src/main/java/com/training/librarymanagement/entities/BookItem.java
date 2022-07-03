package com.training.librarymanagement.entities;

import com.training.librarymanagement.enums.Availability;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

/**
* @generated
*/
@Entity
public class BookItem {

    @Id
    private String code;

    private BigDecimal price;

    @OneToMany(mappedBy = "bookItem")
    private Set<BookReservation> bookReservations = new HashSet<>();

    @ManyToOne(fetch= FetchType.EAGER)
    private Book book;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Set<BookReservation> getBookReservations() {
        return bookReservations;
    }

    public void setBookReservations(Set<BookReservation> bookReservations) {
        this.bookReservations = bookReservations;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
