package com.training.librarymanagement.entities;

import javax.persistence.Entity;

@Entity
public class Member extends Account {
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
