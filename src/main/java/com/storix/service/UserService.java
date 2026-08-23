package com.storix.service;

import com.storix.dto.UserRequest;
import com.storix.dto.UserResponse;
import com.storix.user.User;

public interface UserService {
    UserResponse createUser(UserRequest user);
    UserResponse findByEmail(String email);


}
