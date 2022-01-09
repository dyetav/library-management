package com.training.librarymanagement.entities;

import com.training.librarymanagement.enums.Availability;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import javax.persistence.Entity;
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
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String code;

    private Availability availablity;
    private BigDecimal price;

    @OneToMany(mappedBy = "bookItem")
    private Set<BookReservation> bookReservation;

    @ManyToOne
    private Book book;

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
