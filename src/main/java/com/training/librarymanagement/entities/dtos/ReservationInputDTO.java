package com.training.librarymanagement.entities.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ReservationInputDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date wishedStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date wishedEndDate;

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
