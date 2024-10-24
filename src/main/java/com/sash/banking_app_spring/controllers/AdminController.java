package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.models.User;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/create-admin")
    public String showCreateAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "create-admin";
    }

//    @PostMapping("/create-admin")
//    public String createAdmin(@ModelAttribute User user) {
//        userService.createAdmin(user);  // Use the service method to create an admin
//        return "redirect:/admin";
//    }



}
