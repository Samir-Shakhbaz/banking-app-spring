package com.sash.banking_app_spring.services;

import com.sash.banking_app_spring.models.BankingAccount;
import com.sash.banking_app_spring.models.Transaction;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BankingAccountService {

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    public List<BankingAccount> getAllAccounts() {
        return bankingAccountRepository.findAll();  // Returns all banking accounts
    }

    public BankingAccount createAccount(BankingAccount account) {
        return bankingAccountRepository.save(account);
    }

    public BankingAccount getAccountByAccountNumber(Long accountNumber) {
        return bankingAccountRepository.findByAccountNumber(accountNumber);
    }

    public BankingAccount getAccountById(Long accountId) {
        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
        return accountOpt.orElse(null);
    }

    public BankingAccount deposit(Long accountId, double depositAmount) {
        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            BankingAccount account = accountOpt.get();
            account.setBalance(account.getBalance() + depositAmount);
            account.getTransactionHistory().add(new Transaction("Deposit", depositAmount, LocalDateTime.now(), null));
            return bankingAccountRepository.save(account);
        }
        throw new RuntimeException("Account not found.");
    }

    public String withdraw(Long accountId, double withdrawAmount) {
        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            BankingAccount account = accountOpt.get();
            if (account.getBalance() >= withdrawAmount) {
                account.setBalance(account.getBalance() - withdrawAmount);
                account.getTransactionHistory().add(new Transaction("Withdrawal", withdrawAmount, LocalDateTime.now(), null));
                bankingAccountRepository.save(account);
                return "Withdrawal successful!";
            } else {
                return "Insufficient balance.";
            }
        }
        return "Account not found.";
    }


    public String transfer(Long fromAccountId, Long toAccountId, double amount) {
        Optional<BankingAccount> fromAccountOpt = bankingAccountRepository.findById(fromAccountId);
        Optional<BankingAccount> toAccountOpt = bankingAccountRepository.findById(toAccountId);

        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            BankingAccount fromAccount = fromAccountOpt.get();
            BankingAccount toAccount = toAccountOpt.get();

            if (fromAccount.getBalance() >= amount) {
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);
                bankingAccountRepository.save(fromAccount);
                bankingAccountRepository.save(toAccount);
                return "Transfer successful.";
            }
            return "Insufficient balance.";
        }
        return "One or both accounts not found.";
    }

    public String generateStatement(Long accountNumber, String period) {
        BankingAccount account = getAccountByAccountNumber(accountNumber);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;

        if (period.equalsIgnoreCase("monthly")) {
            startDate = now.minusMonths(1);
        } else if (period.equalsIgnoreCase("yearly")) {
            startDate = now.minusYears(1);
        } else {
            return "Invalid period. Please choose 'monthly' or 'yearly'.";
        }

        List<Transaction> transactions = account.getTransactionHistory();
        StringBuilder statement = new StringBuilder();

        statement.append("Account Statement for ").append(period).append("\n")
                .append("Account Holder: ").append(account.getName()).append("\n")
                .append("Account Number: ").append(account.getAccountNumber()).append("\n")
                .append("Statement Date: ").append(now.toString()).append("\n\n")
                .append("Transactions:\n");

        transactions.stream()
                .filter(t -> t.getDate().isAfter(startDate))
                .forEach(t -> statement.append(t.toString()).append("\n"));

        statement.append("\nCurrent Balance: ").append(account.getBalance());
        return statement.toString();
    }

    public void lockAccount(Long accountId) {
        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            BankingAccount account = accountOpt.get();
            account.setLocked(true);
            account.getNotifications().add("Account locked.");
            bankingAccountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found.");
        }
    }

    public void unlockAccount(Long accountId) {
        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            BankingAccount account = accountOpt.get();
            account.setLocked(false);
            account.getNotifications().add("Account unlocked.");
            bankingAccountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found.");
        }
    }

    public BankingAccount applyInterest(Long accountId, double interestRate) {
        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            BankingAccount account = accountOpt.get();
            double interest = account.getBalance() * (interestRate / 100);
            account.setBalance(account.getBalance() + interest);
            account.getTransactionHistory().add(new Transaction("Interest", interest, LocalDateTime.now(), null));
            return bankingAccountRepository.save(account);
        }
        throw new RuntimeException("Account not found.");
    }

    public List<Transaction> getTransactionHistory(Long accountNumber) {
        BankingAccount account = bankingAccountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new RuntimeException("Account not found");
        }
        return account.getTransactionHistory();
    }

}
