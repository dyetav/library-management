package com.training.librarymanagement.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="account_type",
    discriminatorType = DiscriminatorType.STRING)
public class Account {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(columnDefinition = "CHAR(36)")
    @Id
    private String id;

    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "account")
    private Set<BookReservation> bookReservation;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
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
