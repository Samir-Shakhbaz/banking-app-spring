package com.sash.banking_app_spring.services;

import com.sash.banking_app_spring.models.CheckingAccount;
import com.sash.banking_app_spring.models.SavingsAccount;
import com.sash.banking_app_spring.models.User;
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
            user.setRole("USER");  // Assign a default role if missing
        }

        // Encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Create checking account
        CheckingAccount checkingAccount = new CheckingAccount();
        checkingAccount.setName(user.getUsername() + " Checking Account");
        checkingAccount.setBalance(0.0);
        checkingAccount.setAccountNumber(generateRandomAccountNumber());

        // Create savings account
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setName(user.getUsername() + " Savings Account");
        savingsAccount.setBalance(0.0);
        savingsAccount.setAccountNumber(generateRandomAccountNumber());

        // Save accounts and assign to the user
        bankingAccountRepository.save(checkingAccount);
        bankingAccountRepository.save(savingsAccount);

        user.getAccounts().add(checkingAccount);
        user.getAccounts().add(savingsAccount);

        // Save user with accounts
        return userRepository.save(user);
    }

    private Long generateRandomAccountNumber() {
        // Generate an 8-digit random account number
        return (long) (Math.random() * 90000000) + 10000000;  // Ensures an 8-digit number
    }

}
