package com.sash.banking_app_spring.services;

import com.sash.banking_app_spring.models.*;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import com.sash.banking_app_spring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BankingAccountService {

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    @Autowired
    private UserRepository userRepository;

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

//    public BankingAccount deposit(Long accountId, double depositAmount) {
//        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
//        if (accountOpt.isPresent()) {
//            BankingAccount account = accountOpt.get();
//            account.setBalance(account.getBalance() + depositAmount);
//            account.getTransactionHistory().add(new Transaction("Deposit", depositAmount, LocalDateTime.now(), null));
//            return bankingAccountRepository.save(account);
//        }
//        throw new RuntimeException("Account not found.");
//    }
//
//    public String withdraw(Long accountId, double withdrawAmount) {
//        Optional<BankingAccount> accountOpt = bankingAccountRepository.findById(accountId);
//        if (accountOpt.isPresent()) {
//            BankingAccount account = accountOpt.get();
//            if (account.getBalance() >= withdrawAmount) {
//                account.setBalance(account.getBalance() - withdrawAmount);
//                account.getTransactionHistory().add(new Transaction("Withdrawal", withdrawAmount, LocalDateTime.now(), null));
//                bankingAccountRepository.save(account);
//                return "Withdrawal successful!";
//            } else {
//                return "Insufficient balance.";
//            }
//        }
//        return "Account not found.";
//    }

    public BankingAccount deposit(Long accountId, double depositAmount) {

        BankingAccount account = bankingAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        account.setBalance(account.getBalance() + depositAmount);

        Transaction transaction = new Transaction();
        transaction.setType("Deposit");
        transaction.setAmount(depositAmount);
        transaction.setDate(LocalDateTime.now());
        transaction.setBankingAccount(account);  // Set the relationship between Transaction and BankingAccount

        account.getTransactionHistory().add(transaction);

        return bankingAccountRepository.save(account);
    }

    public String withdraw(Long accountId, double withdrawAmount) {

        BankingAccount account = bankingAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        if (account.getBalance() >= withdrawAmount) {
            account.setBalance(account.getBalance() - withdrawAmount);


            Transaction transaction = new Transaction();
            transaction.setType("Withdrawal");
            transaction.setAmount(withdrawAmount);
            transaction.setDate(LocalDateTime.now());
            transaction.setBankingAccount(account);

            account.getTransactionHistory().add(transaction);

            bankingAccountRepository.save(account);
            return "Withdrawal successful!";
        } else {
            return "Insufficient balance.";
        }
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
            throw new RuntimeException("Account not found.");
        }
        List<Transaction> transactions = account.getTransactionHistory();

        System.out.println("Transaction history for account " + accountNumber + ": " + transactions);

        return transactions;
    }



    public CheckingAccount createCheckingAccount(String name, double balance, Long accountNumber, double overdraftLimit) {
        CheckingAccount account = new CheckingAccount();
        account.setName(name);
        account.setBalance(balance);
        account.setAccountNumber(accountNumber);
        account.setOverdraftLimit(overdraftLimit);
        return bankingAccountRepository.save(account);
    }

    public SavingsAccount createSavingsAccount(String name, double balance, Long accountNumber, double interestRate) {
        SavingsAccount account = new SavingsAccount();
        account.setName(name);
        account.setBalance(balance);
        account.setAccountNumber(accountNumber);
        account.setInterestRate(interestRate);
        return bankingAccountRepository.save(account);
    }

    public void deactivateAccount(Long accountId) {
        BankingAccount account = getAccountById(accountId);
        account.setActive(false);
        account.getNotifications().add("Account deactivated.");
        bankingAccountRepository.save(account);
    }


    public void addNotification(Long accountId, String message) {
        BankingAccount account = getAccountById(accountId);
        account.getNotifications().add(message);
        bankingAccountRepository.save(account);
    }

    public void addAccountToUser(Long userId, Long accountId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BankingAccount account = bankingAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        user.getAccounts().add(account);
        userRepository.save(user); // Save the updated user with the new account link
    }

    public Set<BankingAccount> getUserAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getAccounts(); // Returns all accounts shared with the user
    }


}
