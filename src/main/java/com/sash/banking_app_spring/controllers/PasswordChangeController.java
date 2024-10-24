package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.models.PasswordForm;
import com.sash.banking_app_spring.models.User;
import com.sash.banking_app_spring.repositories.UserRepository;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/change-password")
public class PasswordChangeController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

//    @GetMapping
//    public String showChangePasswordForm(Model model) {
//        model.addAttribute("passwordForm", new PasswordForm());
//        return "change-password";
//    }

//    @PostMapping
//    public String changePassword(@ModelAttribute PasswordForm passwordForm, Principal principal) {
//        String username = principal.getName();
//        userService.changePassword(username, passwordForm.getNewPassword());
//        return "redirect:/home";
//    }

    @GetMapping
    public String showChangePasswordForm(Model model, Principal principal) {
//        if (principal != null) {
//            return "redirect:/home";
//        }

//        User user = userService.findByToken(token);
//        if (user == null) {
//            return "error";
//        }

        model.addAttribute("passwordForm", new PasswordForm());
//        model.addAttribute("token", token);
        return "change-password";
    }



    @PostMapping
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal) {

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/change-password?error=true";
        }

        String username = principal.getName();
        User currentUser = userService.findByUsername(username);

        // Check if password already exists (to ensure uniqueness)
        if (isPasswordUsed(passwordEncoder.encode(newPassword))) {
            return "redirect:/change-password?error=duplicatePassword";
        }

        // Password policy check (minimum length, complexity, etc.)
        if (!isPasswordSafe(newPassword)) {
            return "redirect:/change-password?error=weakPassword";
        }

        // Update the password
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        currentUser.setPasswordChangeRequired(false);
        userService.updateUser(currentUser.getId(), currentUser);

        return "redirect:/accounts/" + currentUser.getId();
    }

    // Example of a password safety check method
    private boolean isPasswordSafe(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";
        return password.matches(passwordPattern);
    }

    public boolean isPasswordUsed(String encodedPassword) {
        // Search for users with the given encoded password
        return userRepository.existsByPassword(encodedPassword);
    }



    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Validate the token
        User user = userService.findByToken(token);
        if (user == null) {
            return "error"; // Handle invalid token scenario
        }

        model.addAttribute("token", token);
        return "reset-password"; // Return the view for reset password
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model) {
        // Validate the token again
        User user = userService.findByToken(token);
        if (user == null) {
            return "error"; // Handle invalid token scenario
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("token", token);
            return "reset-password"; // Return to the form with an error
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Clear the reset token if necessary
        userService.updateUser(user.getId(), user); // Save the updated user

        return "redirect:/login?success=true"; // Redirect to login page
    }

}
