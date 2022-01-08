package com.training.librarymanagement.entities;

import javax.persistence.MappedSuperclass;
import java.util.Set;

@MappedSuperclass
public class Account {
    private String id;
    private String firstName;
    private String lastName;
    private Set<BookReservation> bookReservation;
    private Set<Fine> fine;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<BookReservation> getBookReservation() {
        return bookReservation;
    }

    public void setBookReservation(Set<BookReservation> bookReservation) {
        this.bookReservation = bookReservation;
    }

    public Set<Fine> getFine() {
        return fine;
    }

    public void setFine(Set<Fine> fine) {
        this.fine = fine;
    }
}
