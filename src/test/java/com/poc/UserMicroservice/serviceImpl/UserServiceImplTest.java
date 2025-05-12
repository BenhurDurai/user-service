package com.poc.UserMicroservice.serviceImpl;

import com.poc.UserMicroservice.dto.UserDto;
import com.poc.UserMicroservice.exception.UserAlreadyExistsException;
import com.poc.UserMicroservice.exception.UserNotFoundException;
import com.poc.UserMicroservice.model.Role;
import com.poc.UserMicroservice.model.User;
import com.poc.UserMicroservice.repository.RoleRepository;
import com.poc.UserMicroservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success(){
        UserDto userDto = new UserDto("testuser", "test@example.com", "password@123", "user");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(new Role(1L,"ROLE_USER")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(userDto);

        assertEquals(userDto.getUsername(), registeredUser.getUsername());
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_AdminRoleSuccess() {
        UserDto userDto = new UserDto("adminuser", "admin@example.com", "adminpass", "admin");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        Role adminRole = new Role(2L, "ROLE_ADMIN");
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = userService.registerUser(userDto);

        assertEquals("adminuser", registeredUser.getUsername());
        assertTrue(registeredUser.getRoles().contains(adminRole));
        verify(roleRepository, times(1)).findByRoleName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_AdminRoleNotFound() {
        UserDto userDto = new UserDto("adminuser", "admin@example.com", "adminpass", "admin");

        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerUser(userDto));

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, times(1)).findByRoleName("ROLE_ADMIN");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_UserAlreadyExists(){

        UserDto userDto = new UserDto("existinguser", "existing@example.com", "password123","user");
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userDto));
        verify(userRepository,never()).save(any(User.class));

    }

    @Test
    void testGetAllUsers() {
        List<User> userList = Arrays.asList(
                new User(1L, "user1", "user1@example.com", "password1", new HashSet<>()),
                new User(2L, "user2", "user2@example.com", "password2", new HashSet<>())
        );
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertEquals(2,result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByUsername_Success(){
        User user = new User(1L, "user1" , "user1@example.com", "Password1",new HashSet<>());
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("user1");

        assertEquals("user1", foundUser.getUsername());
        assertEquals("user1@example.com", foundUser.getEmail());
    }

    @Test
    void testGetUserByUsername_UserNotFound(){
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("unknown"));
    }

    @Test
    void testUpdateUserByUsername_Success(){
        User existingUser = new User(1L, "user1", "user1@example.com", "password1",new HashSet<>());
        UserDto userDto = new UserDto("user1", "updateduser1@example.com","updatedPassword1", "admin");
        Role role = new Role(2L, "ROLE_ADMIN");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUserByUsername("user1", userDto);

        assertEquals("updateduser1@example.com", updatedUser.getEmail());
        assertEquals("updatedPassword1", updatedUser.getPassword());
        assertTrue(updatedUser.getRoles().contains(role));
    }

    @Test
    void testUpdatedUserByUsername_UserNotFound(){
        UserDto userDto = new UserDto("user1", "user1@example.com", "password1", "user");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()-> userService.updateUserByUsername("user1", userDto));
    }

    @Test
    void testUpdateUserByUsername_UserRoleNotFound() {
        User existingUser = new User(1L, "user1", "user1@example.com", "password1", new HashSet<>());
        UserDto userDto = new UserDto("user1", "newemail@example.com", "newpass123", "user");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserByUsername("user1", userDto));

        assertEquals("Role not found: user", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserByUsername_AdminRoleNotFound() {
        User existingUser = new User(1L, "adminuser", "admin@example.com", "adminpass", new HashSet<>());
        UserDto userDto = new UserDto("adminuser", "updated@example.com", "adminpass123", "admin");

        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserByUsername("adminuser", userDto));

        assertEquals("Role not found: admin", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserByUsername_UserRoleMissing_ThrowsException() {
        User existingUser = new User(1L, "user1", "user1@example.com", "password1", new HashSet<>());
        UserDto userDto = new UserDto("user1", "new@example.com", "newpassword", "user");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserByUsername("user1", userDto));

        assertEquals("Role not found: user", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRemoveUserByUsername_Success(){
        User user = new User(1L, "user1", "user1@example.com", "password1", new HashSet<>());
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        userService.removeUserByUsername("user1");

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testRemoveUserByUsername_UserNotFound(){
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.removeUserByUsername("unknown"));
    }

}
