package com.training.librarymanagement.entities;

import com.training.librarymanagement.enums.Availability;

import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;

/**
* @generated
*/
public class BookItem extends Book {
    private String code;
    private Availability availablity;
    private BigDecimal price;
    private Set<BookReservation> bookReservation;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Availability getAvailablity() {
        return availablity;
    }

    public void setAvailablity(Availability availablity) {
        this.availablity = availablity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Set<BookReservation> getBookReservation() {
        return bookReservation;
    }

    public void setBookReservation(Set<BookReservation> bookReservation) {
        this.bookReservation = bookReservation;
    }
}
