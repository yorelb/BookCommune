package com.yorelb.book_commune.controller;

import com.yorelb.book_commune.dto.ChangeEmailRequest;
import com.yorelb.book_commune.dto.ChangePasswordRequest;
import com.yorelb.book_commune.dto.LoginRequest;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Add new user
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            User newUser = userService.addUser(user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add user: " + e.getMessage());
        }
    }

    // list of all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    // a specific user's details by its ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User foundUser = userService.findUser(id);
            return ResponseEntity.ok(foundUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Edit a User's details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User savedUser = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a User
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Logging in
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User loggedInUser = userService.verifyLogin(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok(loggedInUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /// Changing email
    @PutMapping("/{id}/email")
    public ResponseEntity<?> changeEmail(@PathVariable Long id, @RequestBody ChangeEmailRequest request) {
        try {
            User updatedUser = userService.changeEmail(
                    id,
                    request.getCurrentEmail(),
                    request.getNewEmail(),
                    request.getPassword()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Change Password
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        try {
            User updatedUser = userService.changePassword(
                    id,
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
