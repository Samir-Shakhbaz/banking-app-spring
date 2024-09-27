package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.models.PasswordForm;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/change-password")
public class PasswordChangeController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "change-password";
    }

    @PostMapping
    public String changePassword(@ModelAttribute PasswordForm passwordForm, Principal principal) {
        String username = principal.getName();
        userService.changePassword(username, passwordForm.getNewPassword());
        return "redirect:/home";
    }
}
