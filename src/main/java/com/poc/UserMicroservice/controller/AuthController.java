package com.poc.UserMicroservice.controller;

import com.poc.commonSecurity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password){
        if ("admin".equals(username) && "password".equals(password)){
            return jwtUtil.generateToken(username);
        }else {
            throw new RuntimeException("Invalid credentials");
        }
    }

}
