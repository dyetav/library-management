package com.training.librarymanagement.entities;


import com.training.librarymanagement.enums.FineStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Fine {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Id
    private String id;

    private BigDecimal price;

    private FineStatus fineStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public FineStatus getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(FineStatus fineStatus) {
        this.fineStatus = fineStatus;
    }
}
