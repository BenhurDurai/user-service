package com.poc.UserMicroservice.controller;

import com.poc.UserMicroservice.dto.UserDto;
import com.poc.UserMicroservice.model.User;
import com.poc.UserMicroservice.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDto userDto){
        log.info("Received request to register user: {}", userDto.getUsername());
        userService.registerUser(userDto);
        log.info("User registered successfully: {}", userDto.getUsername());
        return ResponseEntity.ok("User Registered Successfully");
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUers(){
        log.info("Fetching all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getUser/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        log.info("Fetching user details for username: {}", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> updateUserByUsername(@PathVariable String username, @RequestBody UserDto userDto){
        log.info("Updating user: {}", username);
        User user1 = userService.updateUserByUsername(username,userDto);
        log.info("User updated successfully: {}", user1.getUsername());
        return ResponseEntity.ok(user1);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> removeUserByUsername(@PathVariable String username) {
        log.warn("Deleting user with username: {}", username);
        userService.removeUserByUsername(username);
        log.info("User deleted successfully: {}", username);
        return ResponseEntity.ok("User deleted successfully");

    }
}
