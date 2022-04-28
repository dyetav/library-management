package com.training.librarymanagement.entities.dtos;

import java.util.List;

public class BookItemsDTO {

    private Integer availableItems;

    private List<BookItemNextReservationDTO> nextReservations;

    public Integer getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(Integer availableItems) {
        this.availableItems = availableItems;
    }

    public List<BookItemNextReservationDTO> getNextReservations() {
        return nextReservations;
    }

    public void setNextReservations(List<BookItemNextReservationDTO> nextReservations) {
        this.nextReservations = nextReservations;
    }
}
