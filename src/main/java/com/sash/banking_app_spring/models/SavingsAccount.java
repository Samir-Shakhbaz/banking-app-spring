package com.sash.banking_app_spring.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends BankingAccount {

    private double interestRate;

}
