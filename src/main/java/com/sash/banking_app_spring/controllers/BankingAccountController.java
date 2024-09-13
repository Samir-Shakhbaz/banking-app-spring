package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.client.ExchangeRateAPIClient;
import com.sash.banking_app_spring.client.ExchangeRateResponse;
import com.sash.banking_app_spring.client.Rate;
import com.sash.banking_app_spring.models.BankingAccount;
import com.sash.banking_app_spring.models.Transaction;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import com.sash.banking_app_spring.services.BankingAccountService;
//import com.sash.banking_app_spring.client.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/accounts")
public class BankingAccountController {

    @Autowired
    private BankingAccountService bankingAccountService;

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

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

    @GetMapping("/{accountId}")
    public String showAccount(@PathVariable Long accountId, Model model) {
        BankingAccount account = bankingAccountService.getAccountById(accountId);
        System.out.println("Fetching account with ID: " + accountId);

        if (account != null) {
            model.addAttribute("account", account);
            return "view-account";
        } else {
            model.addAttribute("error", "Account not found");
            return "redirect:/accounts";
        }
    }


    @GetMapping("/create")
    public String showCreateAccountForm(Model model) {
        model.addAttribute("account", new BankingAccount());
        return "create-account";
    }

    @PostMapping("/create")
    public String createAccount(@ModelAttribute BankingAccount account, Model model) {
        bankingAccountService.createAccount(account);
        model.addAttribute("message", "Account created successfully!");
        return "redirect:/accounts/" + account.getAccountNumber();
    }

//    @GetMapping("/{accountNumber}")
//    public String viewAccountDetails(@PathVariable Long accountNumber, Model model) {
//        BankingAccount account = bankingAccountService.getAccountByAccountNumber(accountNumber);
//        model.addAttribute("account", account);
//        return "account-details";
//    }

    @PostMapping("/{accountNumber}/deposit")
    public String deposit(@PathVariable Long accountNumber, @RequestParam double amount, Model model) {
        bankingAccountService.deposit(accountNumber, amount);
        model.addAttribute("message", "Deposit successful!");
        return "redirect:/accounts/" + accountNumber;
    }

    @PostMapping("/{accountNumber}/withdraw")
    public String withdraw(@PathVariable Long accountNumber, @RequestParam double amount, Model model) {
        String message = bankingAccountService.withdraw(accountNumber, amount);
        model.addAttribute("message", message);
        return "redirect:/accounts/" + accountNumber;
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


    @PostMapping("/{accountNumber}/lock")
    public String lockAccount(@PathVariable Long accountNumber, Model model) {
        bankingAccountService.lockAccount(accountNumber);
        model.addAttribute("message", "Account locked successfully.");
        return "redirect:/accounts/" + accountNumber;
    }

    @PostMapping("/{accountNumber}/unlock")
    public String unlockAccount(@PathVariable Long accountNumber, Model model) {
        bankingAccountService.unlockAccount(accountNumber);
        model.addAttribute("message", "Account unlocked successfully.");
        return "redirect:/accounts/" + accountNumber;
    }

    @PostMapping("/generate-statement")
    public String generateStatement(@RequestParam Long accountId, @RequestParam String period, Model model) {
        String statement = bankingAccountService.generateStatement(accountId, period);
        model.addAttribute("statement", statement);
        return "generate-statement";
    }

    @GetMapping("/{accountNumber}/transactions")
    public String viewTransactionHistory(@PathVariable Long accountNumber, Model model) {
        // Fetch the transaction history for the account
        List<Transaction> transactionHistory = bankingAccountService.getTransactionHistory(accountNumber);
        BankingAccount account = bankingAccountService.getAccountByAccountNumber(accountNumber);
        model.addAttribute("account", account);
        model.addAttribute("transactionHistory", transactionHistory);
        return "transaction-history";
    }

}
