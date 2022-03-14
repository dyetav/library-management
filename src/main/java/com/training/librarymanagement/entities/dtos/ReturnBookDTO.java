package com.training.librarymanagement.entities.dtos;

import java.time.LocalDateTime;

public class ReturnBookDTO {

    private LocalDateTime returnDate;

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
}
