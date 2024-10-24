package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.client.ExchangeRateAPIClient;
import com.sash.banking_app_spring.client.ExchangeRateResponse;
import com.sash.banking_app_spring.client.Rate;
import com.sash.banking_app_spring.models.*;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import com.sash.banking_app_spring.repositories.UserRepository;
import com.sash.banking_app_spring.services.BankingAccountService;
//import com.sash.banking_app_spring.client.ExchangeRateService;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/accounts")
public class BankingAccountController {

    @Autowired
    private BankingAccountService bankingAccountService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private ExchangeRateService exchangeRateService;

//    @Autowired
//    private APIExchangeRateService apiExchangeRateService;

//    @GetMapping("/")
//    public String showIndexPage() {
//            return "index";
//        }

    @Autowired
    ExchangeRateAPIClient exchangeRateAPIClient;


    @GetMapping
    public String listAllAccounts(Model model) {
        List<BankingAccount> accounts = bankingAccountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "list-accounts";
    }

    @GetMapping("/my-accounts")
    public String getUserAccounts(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        Set<BankingAccount> accounts = bankingAccountService.getUserAccounts(user.getId());

        model.addAttribute("accounts", accounts);
        return "account-list";
    }

    @GetMapping("/testNavbar")
    public String testNavbar(){
        return "test-navbar";
    }

    @GetMapping("/{userId}")
    public String showAccounts(@PathVariable Long userId, Model model, Principal principal) {
       User user = userRepository.findById(userId).orElse(null);
        System.out.println("Fetching account with ID: " + userId);

        if(user == null || user.getAccounts() == null) {
            model.addAttribute("error", "Account not found");
            return "redirect:/accounts";
        }

        model.addAttribute("user", user);  // Assuming the 'user' has an 'accounts' list
        return "view-account";
    }

//    @GetMapping("/{accountId}")
//    public String showAccountDetails(@PathVariable Long accountId, Model model) {
//        BankingAccount account = bankingAccountService.getAccountById(accountId);
//        if (account == null) {
//            return "redirect:/accounts"; // Redirect if account is not found
//        }
//        model.addAttribute("account", account);
//        return "view-account"; // Stay on the same page and show updated data
//    }


//    @GetMapping("/create")
//    public String showCreateAccountForm(Model model) {
//        model.addAttribute("account", new BankingAccount());
//        return "create-account";
//    }

    @GetMapping("/create")
    public String showCreateAccountForm(Model model) {
        model.addAttribute("checkingAccount", new CheckingAccount());
        model.addAttribute("savingsAccount", new SavingsAccount());
        return "create-account";
    }

    @PostMapping("/create")
    public String createAccount(@ModelAttribute BankingAccount account, Model model) {
        bankingAccountService.createAccount(account);
        model.addAttribute("message", "Account created successfully!");
        return "redirect:/accounts/" + account.getAccountNumber();
    }

    @GetMapping("/create/checking")
    public String showCreateCheckingForm(Model model) {
        model.addAttribute("checkingAccount", new CheckingAccount());
        return "create-checking-account";
    }

    @GetMapping("/create/savings")
    public String showCreateSavingsForm(Model model) {
        model.addAttribute("savingsAccount", new SavingsAccount());
        return "create-savings-account";
    }

    @PostMapping("/create/checking")
    public String createCheckingAccount(@ModelAttribute CheckingAccount checkingAccount) {
        bankingAccountService.createCheckingAccount(checkingAccount.getName(), checkingAccount.getBalance(),
                checkingAccount.getAccountNumber(), checkingAccount.getOverdraftLimit());
        return "redirect:/accounts";
    }

    @PostMapping("/create/savings")
    public String createSavingsAccount(@ModelAttribute SavingsAccount savingsAccount) {
        bankingAccountService.createSavingsAccount(savingsAccount.getName(), savingsAccount.getBalance(),
                savingsAccount.getAccountNumber(), savingsAccount.getInterestRate());
        return "redirect:/accounts";
    }

//    @GetMapping("/{accountNumber}")
//    public String viewAccountDetails(@PathVariable Long accountNumber, Model model) {
//        BankingAccount account = bankingAccountService.getAccountByAccountNumber(accountNumber);
//        model.addAttribute("account", account);
//        return "account-details";
//    }

//    @GetMapping("/{accountId}")
//    public String showAccountDetails(@PathVariable Long accountId, Model model) {
//        BankingAccount account = bankingAccountService.getAccountById(accountId);
//        if (account == null) {
//            return "redirect:/accounts"; // Redirect to the accounts list if account is not found
//        }
//        model.addAttribute("account", account);
//        return "view-account"; // This would be your Thymeleaf template for account details
//    }


    @PostMapping("/{userId}/accounts/{accountId}/deposit")
    public String deposit(@PathVariable Long userId, @PathVariable Long accountId, @RequestParam double amount) {
        bankingAccountService.deposit(accountId, amount);
        // Redirect back to the user's accounts page
        return "redirect:/accounts/" + userId;
    }


    @PostMapping("/{userId}/accounts/{accountId}/withdraw")
    public String withdraw(@PathVariable Long userId, @PathVariable Long accountId, @RequestParam double amount) {
        bankingAccountService.withdraw(accountId, amount);
        return "redirect:/accounts/" + userId;
    }


    @GetMapping("/{accountNumber}/statement")
    public String viewStatement(@PathVariable Long accountNumber, @RequestParam String period, Model model) {
        BankingAccount account = bankingAccountService.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            model.addAttribute("errorMessage", "Account not found");
            return "error";
        }

        String statement = bankingAccountService.generateStatement(accountNumber, period);

        model.addAttribute("account", account);
        model.addAttribute("period", period);
        model.addAttribute("statement", statement);

        return "account-statement";
    }

    @GetMapping("/lock-unlock")
    public String showLockUnlockPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        // Fetch the user's accounts
        Set<BankingAccount> accounts = user.getAccounts();
        model.addAttribute("accounts", accounts);

        return "lock-unlock-account";
    }


    @PostMapping("/{accountNumber}/lock")
    public String lockAccount(@PathVariable Long accountNumber, Model model) {
        bankingAccountService.lockAccount(accountNumber);
        model.addAttribute("message", "Account locked successfully.");
        return "redirect:/accounts/lock-unlock";
    }

    @PostMapping("/{accountNumber}/unlock")
    public String unlockAccount(@PathVariable Long accountNumber, Model model) {
        bankingAccountService.unlockAccount(accountNumber);
        model.addAttribute("message", "Account unlocked successfully.");
        return "redirect:/accounts/lock-unlock";
    }

    //LOCK-UNLOCK COMBINED
//    @PostMapping("/{accountNumber}/{action}")
//    public String lockOrUnlockAccount(@PathVariable Long accountNumber, @PathVariable String action, Model model) {
//        if ("lock".equalsIgnoreCase(action)) {
//            bankingAccountService.lockAccount(accountNumber);
//            model.addAttribute("message", "Account locked successfully.");
//        } else if ("unlock".equalsIgnoreCase(action)) {
//            bankingAccountService.unlockAccount(accountNumber);
//            model.addAttribute("message", "Account unlocked successfully.");
//        } else {
//            model.addAttribute("error", "Invalid action.");
//            return "redirect:/accounts/lock-unlock";
//        }
//        return "redirect:/accounts/lock-unlock";
//    }


    @PostMapping("/generate-statement")
    public String generateStatement(@RequestParam Long accountId, @RequestParam String period, Model model) {
        String statement = bankingAccountService.generateStatement(accountId, period);
        model.addAttribute("statement", statement);
        return "generate-statement";
    }

//    @GetMapping("/{accountNumber}/transactions")
//    public String viewTransactionHistory(@PathVariable Long accountNumber, Model model) {
//        List<Transaction> transactionHistory = bankingAccountService.getTransactionHistory(accountNumber);
//        BankingAccount account = bankingAccountService.getAccountByAccountNumber(accountNumber);
//        model.addAttribute("account", account);
//        model.addAttribute("transactionHistory", transactionHistory);
//        return "transaction-history";
//    }

    @GetMapping("/{accountNumber}/transactions")
    public String viewTransactionHistory(@PathVariable Long accountNumber, Model model) {
        BankingAccount account = bankingAccountService.getAccountByAccountNumber(accountNumber);

        if (account != null) {
            List<Transaction> transactionHistory = account.getTransactionHistory();
            model.addAttribute("account", account);
            model.addAttribute("transactionHistory", transactionHistory);
        } else {
            model.addAttribute("errorMessage", "Account not found.");
        }

        return "transaction-history";
    }

    @PostMapping("/{accountId}/deactivate")
    public String deactivateAccount(@PathVariable Long accountId, Model model) {
        bankingAccountService.deactivateAccount(accountId);
        model.addAttribute("message", "Account has been deactivated.");
        return "redirect:/accounts/" + accountId;
    }

//    @GetMapping("/{accountId}/notifications")
//    public String viewNotifications(@PathVariable Long accountId, Model model) {
//        BankingAccount account = bankingAccountService.getAccountById(accountId);
//        model.addAttribute("account", account);
//        model.addAttribute("notifications", account.getNotifications());
//        return "notifications";
//    }

//    @PostMapping("/create-savings")
//    public String createSavingsAccount(@RequestParam String name, @RequestParam double initialDeposit, Long accountNumber, @RequestParam double interestRate) {
//        bankingAccountService.createSavingsAccount(name, initialDeposit, accountNumber, interestRate);
//        return "redirect:/accounts";
//    }
//
//    @PostMapping("/create-checking")
//    public String createCheckingAccount(@RequestParam String name, @RequestParam double initialDeposit, Long accountNumber, @RequestParam double overdraftLimit) {
//        bankingAccountService.createCheckingAccount(name, initialDeposit, accountNumber, overdraftLimit);
//        return "redirect:/accounts";
//    }


    @GetMapping("/{userId}/transfer")
    public String showTransferForm(@PathVariable Long userId, Model model, Principal principal) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || user.getAccounts().isEmpty()) {
            model.addAttribute("error", "Account not found or does not belong to this user.");
            return "redirect:/user/" + userId + "/accounts";
        }

        model.addAttribute("user", user);
        model.addAttribute("accounts", user.getAccounts());

        return "transfer-money";
    }

    @PostMapping("/{userId}/transfer")
    public String transfer(@PathVariable Long userId,
                           @RequestParam Long sourceAccountId,
                           @RequestParam Long targetAccountId,
                           @RequestParam double amount, Model model) {

        String result = bankingAccountService.transfer(sourceAccountId, targetAccountId, amount);


        model.addAttribute("message", result);
        return "redirect:/accounts/" + userId;
    }

    @GetMapping("/{userId}/add-user")
    public String showAddUserForm(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || user.getAccounts().isEmpty()) {
            model.addAttribute("error", "User or Accounts not found.");
            return "redirect:/accounts/" + userId;
        }

        BankingAccount checkingAccount = user.getAccounts().stream()
                .filter(account -> account instanceof CheckingAccount)
                .findFirst()
                .orElse(null);

        BankingAccount savingsAccount = user.getAccounts().stream()
                .filter(account -> account instanceof SavingsAccount)
                .findFirst()
                .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("checkingAccount", checkingAccount);
        model.addAttribute("savingsAccount", savingsAccount);

        return "add-user";
    }

    @PostMapping("/{userId}/add-user")
    public String addUserToExistingAccounts(@RequestParam String newUsername,
                                            @RequestParam String newPassword,
                                            @RequestParam List<Long> accountSelection,
                                            @PathVariable Long userId, Model model) {

        User newUser = new User();
        newUser.setUsername(newUsername);
        newUser.setPassword(newPassword);
        newUser.setRole("USER");

        userService.addUserToExistingAccounts(newUser, accountSelection);

        return "redirect:/accounts/" + userId;
    }

    @GetMapping("/{userId}/notifications")
    public String showNotificationSettings(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId).orElse(null);
        NotificationSettings settings = user.getNotificationSettings();

        if (settings == null) {
            settings = new NotificationSettings();
            settings.setLoginNotification(false);
            settings.setCheckingAccountNotification(false);
            settings.setSavingsAccountNotification(false);
            settings.setEmailNotification(false);
            settings.setPhoneNotification(false);
            user.setNotificationSettings(settings);
            userService.updateNotificationSettings(user); // Save the new settings
        }

        model.addAttribute("user", user);
        model.addAttribute("settings", settings);

        return "notification-settings";
    }

    @PostMapping("/{userId}/notifications")
    public String updateNotificationSettings(@PathVariable Long userId,
                                             @RequestParam(required = false) Boolean loginNotification,
                                             @RequestParam(required = false) Boolean checkingAccountNotification,
                                             @RequestParam(required = false) Boolean savingsAccountNotification,
                                             @RequestParam(required = false) Boolean emailNotification,
                                             @RequestParam(required = false) Boolean phoneNotification) {

        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        NotificationSettings settings = user.getNotificationSettings();
        if (settings == null) {
            settings = new NotificationSettings();
            user.setNotificationSettings(settings);
        }

        settings.setLoginNotification(Boolean.TRUE.equals(loginNotification));
        settings.setCheckingAccountNotification(Boolean.TRUE.equals(checkingAccountNotification));
        settings.setSavingsAccountNotification(Boolean.TRUE.equals(savingsAccountNotification));
        settings.setEmailNotification(Boolean.TRUE.equals(emailNotification));
        settings.setPhoneNotification(Boolean.TRUE.equals(phoneNotification));

        userService.updateNotificationSettings(user);

        return "redirect:/accounts/" + userId;
    }

    @PostMapping("/{accountId}/delete")
    public String deleteUser(@PathVariable Long accountId) {
        bankingAccountService.deleteAccount(accountId);
        return "redirect:/accounts";
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String username, Model model) {
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "forgot-password";
        }

        String resetToken = userService.generatePasswordResetToken(user);
        userService.sendPasswordResetEmail(user, resetToken);

        model.addAttribute("success", "Password reset instructions sent to your email");
        return "forgot-password";
    }

}
