package com.storix.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
public class UserRequest {


   @NotBlank(message = "Email is required")
   @Email(message = "Please provide me a valid email")
    private String email;
   @NotBlank(message = "Password is required")
   @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

}
