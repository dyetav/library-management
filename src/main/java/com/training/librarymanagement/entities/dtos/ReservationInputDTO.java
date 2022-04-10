package com.training.librarymanagement.entities.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ReservationInputDTO {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date wishedStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date wishedEndDate;

    public ReservationInputDTO(Date startDate, Date endDate) {
        this.wishedEndDate = endDate;
        this.wishedStartDate = startDate;
    }

    public Date getWishedStartDate() {
        return wishedStartDate;
    }

    public Date getWishedEndDate() {
        return wishedEndDate;
    }

}