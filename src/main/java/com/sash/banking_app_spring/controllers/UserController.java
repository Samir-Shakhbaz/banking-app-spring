package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.models.User;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 Authentication authentication,
                                 Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (!userService.checkIfValidOldPassword(user, currentPassword)) {
            model.addAttribute("error", "Current password is incorrect.");
            return "change-password";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            return "change-password";
        }

        if (newPassword.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters long.");
            return "change-password";
        }

        userService.changeUserPassword(user, newPassword);
        model.addAttribute("success", "Password changed successfully.");
        return "change-password";
    }
}
