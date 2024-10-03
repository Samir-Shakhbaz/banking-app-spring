package com.sash.banking_app_spring.services;

import com.sash.banking_app_spring.models.*;
import com.sash.banking_app_spring.repositories.BankingAccountRepository;
import com.sash.banking_app_spring.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Transactional
//    public void createUser(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//    }

    @Transactional
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordChangeRequired(true);
        userRepository.save(user);
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setPassword(updatedUser.getPassword());
                    user.setRole(updatedUser.getRole());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        userRepository.save(user);
    }

    @Transactional
    public User createUserWithAccounts(User user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        CheckingAccount checkingAccount = new CheckingAccount();
        checkingAccount.setName(user.getUsername() + " Checking Account");
        checkingAccount.setBalance(0.0);
        checkingAccount.setAccountNumber(generateRandomAccountNumber());

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setName(user.getUsername() + " Savings Account");
        savingsAccount.setBalance(0.0);
        savingsAccount.setAccountNumber(generateRandomAccountNumber());

        NotificationSettings notificationSettings = new NotificationSettings();
        notificationSettings.setLoginNotification(false);
        notificationSettings.setCheckingAccountNotification(false);
        notificationSettings.setSavingsAccountNotification(false);
        notificationSettings.setEmailNotification(false);
        notificationSettings.setPhoneNotification(false);

        // Set notification settings to user
        user.setNotificationSettings(notificationSettings);

        bankingAccountRepository.save(checkingAccount);
        bankingAccountRepository.save(savingsAccount);

        user.getAccounts().add(checkingAccount);
        user.getAccounts().add(savingsAccount);

        return userRepository.save(user);
    }

    private Long generateRandomAccountNumber() {
        // generate an 8-digit random account number
        return (long) (Math.random() * 90000000) + 10000000;  // Ensures an 8-digit number
    }

    @Transactional
    public User addUserToExistingAccounts(User newUser, List<Long> accountIds) {

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        List<BankingAccount> accounts = bankingAccountRepository.findAllById(accountIds);

        newUser.getAccounts().addAll(accounts);

        return userRepository.save(newUser);
    }

    public void updateNotificationSettings(User user) {
        userRepository.save(user);
    }

}
