package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private double amount;

    private LocalDateTime date;

    @Column(name = "target_account")
    private String targetAccount;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private BankingAccount bankingAccount;

    public Transaction() {}

    public Transaction(String type, double amount, LocalDateTime date, String targetAccount) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.targetAccount = targetAccount;
    }
}
