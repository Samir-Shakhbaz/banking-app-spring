package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // here we can choose JOINED or TABLE_PER_CLASS based on design
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class BankingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal balance = BigDecimal.ZERO;
    @Column(unique = true)
    private Long accountNumber;
    private boolean active;
    private boolean locked;
    private BigDecimal totalExpenses;
    private BigDecimal budget;
    private BigDecimal savingsBalance;
    private double savingsPercentage;


    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactionHistory = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "overdraft_account_id")
    private BankingAccount overdraftAccount;

    @ElementCollection
    private List<String> notifications = new ArrayList<>();

    @ManyToMany(mappedBy = "accounts")
    private Set<User> users = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber); // Exclude 'users' to avoid recursion
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BankingAccount)) return false;
        BankingAccount other = (BankingAccount) obj;
        return Objects.equals(id, other.id) && Objects.equals(accountNumber, other.accountNumber); // Exclude 'users'
    }

}
