package com.poc.UserMicroservice.controller;

import com.poc.UserMicroservice.dto.UserDto;
import com.poc.UserMicroservice.model.Role;
import com.poc.UserMicroservice.model.User;
import com.poc.UserMicroservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User sampleUser;
    private UserDto sampleUserDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        sampleUserDto = new UserDto("testUser", "test@example.com", "password123", "User");
        sampleUser = new User(1L, "testUser", "test@example.com", "password123", Set.of(new Role(1L, "ROLE_USER")));
    }

    @Test
    public void testRegisterUser() {
        ResponseEntity<String> response = userController.registerUser(sampleUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User Registered Successfully", response.getBody());
        verify(userService, times(1)).registerUser(sampleUserDto);
    }

    @Test
    public void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("testUser", response.getBody().get(0).getUsername());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserByUsername() {
        when(userService.getUserByUsername("testUser")).thenReturn(sampleUser);

        ResponseEntity<User> response = userController.getUserByUsername("testUser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).getUserByUsername("testUser");
    }

    @Test
    public void testUpdateUserByUsername() {
        when(userService.updateUserByUsername("testUser", sampleUserDto)).thenReturn(sampleUser);

        ResponseEntity<User> response = userController.updateUserByUsername("testUser", sampleUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userService, times(1)).updateUserByUsername("testUser", sampleUserDto);
    }

    @Test
    public void testRemoveUserByUsername() {
        doNothing().when(userService).removeUserByUsername("testUser");

        ResponseEntity<String> response = userController.removeUserByUsername("testUser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).removeUserByUsername("testUser");
    }
}
