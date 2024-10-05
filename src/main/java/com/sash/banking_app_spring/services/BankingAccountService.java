package com.sash.banking_app_spring.services;

import com.sash.banking_app_spring.models.*;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import com.sash.banking_app_spring.repositories.UserRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

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

        // Find the account by ID or throw an exception if not found
        BankingAccount account = bankingAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        // Update the account balance
        account.setBalance(account.getBalance() + depositAmount);

        // Create a new transaction and associate it with the account
        Transaction transaction = new Transaction();
        transaction.setType("Deposit");
        transaction.setAmount(depositAmount);
        transaction.setDate(LocalDateTime.now());
        transaction.setBankingAccount(account);  // Set the relationship between Transaction and BankingAccount

        // Add the transaction to the account's transaction history
        account.getTransactionHistory().add(transaction);

        // Fetch the user associated with the account
        User user = getUserByAccount(accountId);

        // Ensure the user has NotificationSettings initialized
        NotificationSettings notificationSettings = user.getNotificationSettings();
        if (notificationSettings == null) {
            // Initialize notification settings if they are null (fallback to default values)
            notificationSettings = new NotificationSettings();
            notificationSettings.setEmailNotification(false); // Default value
            notificationSettings.setPhoneNotification(false); // Default value
            user.setNotificationSettings(notificationSettings);
            userRepository.save(user); // Save the user with initialized notification settings
        }

        // Check if email notification is enabled and send notification if it is
        if (notificationSettings.isEmailNotification()) {
            emailService.sendNotificationEmail(user.getEmail(),
                    "Deposit Notification",
                    "A deposit of $" + depositAmount + " has been made to your account."
            );
        }

        // Check if phone notification is enabled and send notification if it is
        if (notificationSettings.isPhoneNotification()) {
            smsService.sendNotification(user.getPhone(),
                    "A deposit of $" + depositAmount + " has been made to your account.");
        }

        // Save the updated account (including transaction history) and return the result
        return bankingAccountRepository.save(account);
    }



    public User getUserByAccount(Long accountId) {
        BankingAccount account = bankingAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        return account.getUsers().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No user associated with this account."));
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

//    public String transfer(Long fromAccountId, Long toAccountId, double amount) {
//        Optional<BankingAccount> fromAccountOpt = bankingAccountRepository.findById(fromAccountId);
//        Optional<BankingAccount> toAccountOpt = bankingAccountRepository.findById(toAccountId);
//
//        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
//            BankingAccount fromAccount = fromAccountOpt.get();
//            BankingAccount toAccount = toAccountOpt.get();
//
//            if (fromAccount.getBalance() >= amount) {
//                fromAccount.setBalance(fromAccount.getBalance() - amount);
//                toAccount.setBalance(toAccount.getBalance() + amount);
//                bankingAccountRepository.save(fromAccount);
//                bankingAccountRepository.save(toAccount);
//                return "Transfer successful.";
//            }
//            return "Insufficient balance.";
//        }
//        return "One or both accounts not found.";
//    }

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

    public void lockAccount(Long accountNumber) {
        Optional<BankingAccount> accountOpt = Optional.ofNullable(bankingAccountRepository.findByAccountNumber(accountNumber));
        if (accountOpt.isPresent()) {
            BankingAccount account = accountOpt.get();
            account.setLocked(true);
            account.getNotifications().add("Account locked.");
            bankingAccountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found.");
        }
    }


    public void unlockAccount(Long accountNumber) {
        Optional<BankingAccount> accountOpt = Optional.ofNullable(bankingAccountRepository.findByAccountNumber(accountNumber));
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
        userRepository.save(user);
    }

    public Set<BankingAccount> getUserAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getAccounts();
    }

    public String transfer(Long sourceAccountId, Long targetAccountId, double amount) {
        BankingAccount sourceAccount = bankingAccountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        BankingAccount targetAccount = bankingAccountRepository.findById(targetAccountId)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (sourceAccount.getBalance() < amount) {
            return "Insufficient funds in the source account";
        }

        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
        targetAccount.setBalance(targetAccount.getBalance() + amount);

        bankingAccountRepository.save(sourceAccount);
        bankingAccountRepository.save(targetAccount);

        return "Transfer successful!";
    }

    @Transactional
    public void addUserToAccount(Long userId, Long accountId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankingAccount account = bankingAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        user.getAccounts().add(account);

        account.getUsers().add(user);

        userRepository.save(user);
        bankingAccountRepository.save(account);
    }


}
