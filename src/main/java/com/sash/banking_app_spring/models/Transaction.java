package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private BigDecimal amount;

    private LocalDateTime date;

    @Column(name = "target_account")
    private Long targetAccount;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private BankingAccount bankingAccount;

    public Transaction() {}

    public Transaction(String type, BigDecimal amount, LocalDateTime date, Long targetAccount) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.targetAccount = targetAccount;
    }
}
