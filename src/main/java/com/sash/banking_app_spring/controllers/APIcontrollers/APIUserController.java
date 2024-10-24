package com.sash.banking_app_spring.controllers.APIcontrollers;

import com.sash.banking_app_spring.models.User;
import com.sash.banking_app_spring.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class APIUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/list", produces = "application/json")
    public List<User> listUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createUserWithAccounts(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {

        userService.createUser(username, password, role, email, phone);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/profile")
    public User getUserProfile(Principal principal) {
        String username = principal.getName();
        return userService.findByUsername(username);
    }

    @PutMapping("/profile")
    public User updateProfile(@RequestBody User user, Principal principal) {
        String currentUsername = principal.getName();
        User currentUser = userService.findByUsername(currentUsername);

        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());

        return userService.updateUser(currentUser.getId(), currentUser);
    }


}
