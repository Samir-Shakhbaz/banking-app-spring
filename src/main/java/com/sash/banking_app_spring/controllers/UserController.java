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

import java.security.Principal;
import java.util.List;

//@RestController
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
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {

        userService.createUser(username, password, role, email, phone);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "list-users";
    }


//    //REST CONTROLLER
//    @GetMapping(value = "/list", produces = "application/json")
//    public List<User> listUsers() {
//        return userService.getAllUsers();
//    }

    @PostMapping("/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/user/list";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User user, Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());

        userService.updateUser(currentUser.getId(), currentUser);

        return "redirect:/profile?success";
    }



}
