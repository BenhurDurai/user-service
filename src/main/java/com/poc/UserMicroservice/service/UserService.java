package com.poc.UserMicroservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.poc.UserMicroservice.dto.UserDto;
import com.poc.UserMicroservice.exception.UserAlreadyExistsException;
import com.poc.UserMicroservice.exception.UserNotFoundException;
import com.poc.UserMicroservice.model.User;
import com.poc.UserMicroservice.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User registerUser(@Valid UserDto userDto) {
        log.info("Attempting to register user: {}", userDto.getUsername());

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()){
            log.warn("Registration failed. Username '{}' already exists", userDto.getUsername());
            throw new UserAlreadyExistsException("Username already taken");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        log.info("User '{}' registered successfully", user.getUsername());
        return userRepository.save(user);

    }

    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(()-> {
                    log.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });
    }

    public User updateUserByUsername(String username, UserDto userDto) {
        log.info("Attempting to update user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> {
                    log.error("Update failed. Username '{}' not found", username);
                    return new UserNotFoundException("Username not matched with the records");
                });

        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        log.info("User '{}' updated successfully", user.getUsername());
        return userRepository.save(user);
    }

    public void removeUserByUsername(String username) {
        log.info("Attempting to delete user with username: {}", username);
        User user1 = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Deletion failed. User '{}' not found", username);
                    return new UserNotFoundException("User not found with " + username);
                });
        userRepository.delete(user1);
        log.info("User '{}' deleted successfully", username);
    }


}
