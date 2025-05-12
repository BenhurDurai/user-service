package com.poc.UserMicroservice.controller;


import com.poc.UserMicroservice.model.Role;
import com.poc.UserMicroservice.model.User;
import com.poc.UserMicroservice.service.UserService;
import com.poc.commonSecurity.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    private JwtUtil jwtUtil;
    private UserService userService;
    private AuthController authController;

    @BeforeEach
    public void setup(){
        jwtUtil = mock(JwtUtil.class);
        userService = mock(UserService.class);
        authController = new AuthController(jwtUtil, userService);
    }

    @Test
    public void testLogin_Success(){
        String username = "testUser";
        String password = "password123";
        Role role = new Role(1L, "ROLE_USER");
        User user = new User(1L, username, "test@example.com", password, Set.of(role));

        when(userService.getUserByUsername(username)).thenReturn(user);
        when(jwtUtil.generateToken(eq(username),anyList())).thenReturn("mocked_jwt_token");

        String token = authController.login(username, password);

        assertNotNull(token);
        assertEquals("mocked_jwt_token", token);
        verify(userService,times(1)).getUserByUsername(username);
        verify(jwtUtil, times(1)).generateToken(eq(username),eq(List.of("ROLE_USER")));

    }

    @Test
    public void testLogin_InvalidPassword(){
        String username = "testUser";
        String actualPassword = "password123";
        String providedPassword = "wrongpassword";

        Role role = new Role(1L, "ROLE_USER");
        User user = new User(1L, username, "test@example.com", actualPassword, Set.of(role));

        when(userService.getUserByUsername(username)).thenReturn(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authController.login(username, providedPassword));

        assertEquals("Invalid Credentials", exception.getMessage());
        verify(userService, times(1)).getUserByUsername(username);
        verify(jwtUtil,never()).generateToken(any(),anyList());
    }

}
