package com.sash.banking_app_spring.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

//@EqualsAndHashCode(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@DiscriminatorValue("CHECKING")
public class CheckingAccount extends BankingAccount {

    private BigDecimal overdraftLimit;

}
