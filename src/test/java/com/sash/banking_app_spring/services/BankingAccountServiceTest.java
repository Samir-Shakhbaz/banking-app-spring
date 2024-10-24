package com.sash.banking_app_spring.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.sash.banking_app_spring.models.BankingAccount;
import com.sash.banking_app_spring.models.CheckingAccount;
import com.sash.banking_app_spring.models.NotificationSettings;
import com.sash.banking_app_spring.models.User;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import com.sash.banking_app_spring.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class BankingAccountServiceTest {

    @Mock
    private BankingAccountRepository bankingAccountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SMSService smsService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BankingAccountService bankingAccountService;

    private User user;
    private BankingAccount account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a mock user and set up notification settings
        user = new User();
        user.setId(1L);
        user.setPhone("+1234567890");

        NotificationSettings notificationSettings = new NotificationSettings();
        notificationSettings.setPhoneNotification(true); // Enable phone notification for testing
        user.setNotificationSettings(notificationSettings);

        // Create a mock banking account
        account = new CheckingAccount();
        account.setId(1L);
        account.setBalance(100.0);
        account.setTransactionHistory(new ArrayList<>());

        // Add the user to the account's users set
        Set<User> accountUsers = new HashSet<>();
        accountUsers.add(user);
        account.setUsers(accountUsers);

        // Mock the account repository to return the account when accountId = 1
        when(bankingAccountRepository.findById(1L)).thenReturn(Optional.of(account));

//        // Mock the getUserByAccount method to return the user
//        when(bankingAccountService.getUserByAccount(1L)).thenReturn(user);
    }

    @Test
    void testDepositPhoneNotification() {
        // Mock finding the account by ID
        when(bankingAccountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Perform the deposit action
        double depositAmount = 50.0;
        bankingAccountService.deposit(1L, depositAmount);

        // Verify that the phone notification (SMS) is sent
        verify(smsService).sendNotification(eq(user.getPhone()), anyString());

        // Verify that the account balance is updated correctly
        assertEquals(150.0, account.getBalance(), "Account balance should be updated after deposit");

        // Verify that the account is saved with the updated balance and transaction history
        verify(bankingAccountRepository).save(account);
    }
}
