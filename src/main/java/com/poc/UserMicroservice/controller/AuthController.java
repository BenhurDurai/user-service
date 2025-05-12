package com.poc.UserMicroservice.controller;

import com.poc.UserMicroservice.model.Role;
import com.poc.UserMicroservice.model.User;
import com.poc.UserMicroservice.service.UserService;
import com.poc.commonSecurity.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private JwtUtil jwtUtil;
    private UserService userService;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password){
        log.info("Received request to generate jwt token for username :{}", username);
        User user = userService.getUserByUsername(username);
        if (user.getPassword().equals(password)){
            List<String> roles = user.getRoles().stream()
                    .map(Role::getRoleName)
                    .toList();
            return jwtUtil.generateToken(username,roles);
        }else {
            throw new RuntimeException("Invalid Credentials");
        }
    }


}
