package com.training.librarymanagement.entities.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;


import java.util.Date;

public class BookInputDTO {

    @NotNull
    private String ISBN;

    private String rackNumber;

    @NotNull
    private String title;

    @NotNull
    private String subjectCategory;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publicationDate;

    @NotNull
    private String authorId;

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

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}
