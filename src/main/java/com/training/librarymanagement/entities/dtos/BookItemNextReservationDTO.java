package com.training.librarymanagement.entities.dtos;

import java.util.Date;

public class BookItemNextReservationDTO {

    private String code;

    private Date wishedStartDate;

    private Date wishedEndDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getWishedStartDate() {
        return wishedStartDate;
    }

    public void setWishedStartDate(Date wishedStartDate) {
        this.wishedStartDate = wishedStartDate;
    }

    public Date getWishedEndDate() {
        return wishedEndDate;
    }

    public void setWishedEndDate(Date wishedEndDate) {
        this.wishedEndDate = wishedEndDate;
    }
}
