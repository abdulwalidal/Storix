package com.storix.dto;

import com.storix.user.Role;
import com.storix.user.User;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Value;



@Data
public class UserResponse {

    private Long id;
    private String email;
    private Role role;
}