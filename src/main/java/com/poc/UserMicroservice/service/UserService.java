package com.poc.UserMicroservice.service;

import com.poc.UserMicroservice.dto.UserDto;
import com.poc.UserMicroservice.model.User;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    User registerUser(UserDto userDto);
    List<User> getAllUsers();
    User getUserByUsername(String username);
    User updateUserByUsername(String username, UserDto userDto);
    void removeUserByUsername(String username);

}
