package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.models.User;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "create-user-and-account";
    }

//    @PostMapping("/create")
//    public String createUser(@ModelAttribute User user) {
//        userService.createUser(user);
//        return "redirect:/accounts";
//    }

//    @PostMapping("/create")
//    public String createUser(@ModelAttribute User user) {
//        userService.createUserWithAccounts(user);
//        return "redirect:/login";  // Redirect to login after user creation
//    }

        @PostMapping("/create")
        public String createUserWithAccounts(
                @RequestParam String username,
                @RequestParam String password,
                @RequestParam String role) {

            // Create a new User object and set the username and password
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);// The service will handle password encoding
            user.setRole(role);

            // Call the service to create the user with checking and savings accounts
            userService.createUserWithAccounts(user);

            // Redirect to login or home page after success
            return "redirect:/";
        }


    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {

            return "redirect:/change-password?error=true";
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        currentUser.setPasswordChangeRequired(false);
        userService.updateUser(currentUser.getId(), currentUser);

        return "redirect:/accounts";
    }

    @GetMapping("/list")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "list-users";
    }

}
