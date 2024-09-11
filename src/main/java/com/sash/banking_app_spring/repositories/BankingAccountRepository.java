package com.sash.banking_app_spring.repositories;

import com.sash.banking_app_spring.models.BankingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingAccountRepository extends JpaRepository<BankingAccount, Long> {
    BankingAccount findByAccountNumber(Long accountNumber);
}

