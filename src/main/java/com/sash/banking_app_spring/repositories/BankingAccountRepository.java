package com.sash.banking_app_spring.repositories;

import com.sash.banking_app_spring.models.BankingAccount;
import com.sash.banking_app_spring.models.CheckingAccount;
import com.sash.banking_app_spring.models.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankingAccountRepository extends JpaRepository<BankingAccount, Long> {
    BankingAccount findByAccountNumber(Long accountNumber);
    List<CheckingAccount> findByOverdraftLimitGreaterThan(double limit);
    List<SavingsAccount> findByInterestRateGreaterThan(double rate);
}

