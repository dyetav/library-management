package com.training.librarymanagement.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class BookReservation {

    @Id
    private String id;

    private Date startBookingDate;
    private Date endBookingDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "book_code")
    private BookItem bookItem;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartBookingDate() {
        return startBookingDate;
    }

    public void setStartBookingDate(Date startBookingDate) {
        this.startBookingDate = startBookingDate;
    }

    public Date getEndBookingDate() {
        return endBookingDate;
    }

    public void setEndBookingDate(Date endBookingDate) {
        this.endBookingDate = endBookingDate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }
}
