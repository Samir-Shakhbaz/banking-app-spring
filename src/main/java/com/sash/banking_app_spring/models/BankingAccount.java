package com.sash.banking_app_spring.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // here we can choose JOINED or TABLE_PER_CLASS based on design
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class BankingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double balance;
    @Column(unique = true)
    private Long accountNumber;
    private boolean active;
    private boolean locked;
    private double totalExpenses;
    private double budget;
    private double savingsBalance;
    private double savingsPercentage;


    @OneToMany(mappedBy = "bankingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactionHistory = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "overdraft_account_id")
    private BankingAccount overdraftAccount;

    @ElementCollection
    private List<String> notifications = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "joint_account_holders",
            joinColumns = @JoinColumn(name = "banking_account_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> jointAccountHolders = new ArrayList<>();

}
