package com.poc.UserMicroservice.dto;

import com.poc.UserMicroservice.annotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor


public class UserDto {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email is required")
    private String email;

    @ValidPassword
    private String password;

}
