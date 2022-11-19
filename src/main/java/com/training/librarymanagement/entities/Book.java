package com.training.librarymanagement.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Book {

    @Id
    private String ISBN;

    private String rackNumber;

    @Column(unique = true)
    private String title;
    private String subjectCategory;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<BookItem> items;

    @ManyToOne
    private Author author;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publicationDate;

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(String rackNumber) {
        this.rackNumber = rackNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubjectCategory() {
        return subjectCategory;
    }

    public void setSubjectCategory(String subjectCategory) {
        this.subjectCategory = subjectCategory;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Set<BookItem> getItems() {
        return items;
    }

    public void setItems(Set<BookItem> items) {
        this.items = items;
    }
}
