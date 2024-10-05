package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.models.PasswordForm;
import com.sash.banking_app_spring.models.User;
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

    @GetMapping
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "change-password";
    }

//    @PostMapping
//    public String changePassword(@ModelAttribute PasswordForm passwordForm, Principal principal) {
//        String username = principal.getName();
//        userService.changePassword(username, passwordForm.getNewPassword());
//        return "redirect:/home";
//    }




    @PostMapping
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal) {

        if (!newPassword.equals(confirmPassword)) {

            return "redirect:/change-password?error=true";
        }

        String username = principal.getName();
        User currentUser = userService.findByUsername(username);

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        currentUser.setPasswordChangeRequired(false);
        userService.updateUser(currentUser.getId(), currentUser);

        return "redirect:/accounts/" + currentUser.getId();
    }

}
