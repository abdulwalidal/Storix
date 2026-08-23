package com.storix.controller;


import com.storix.dto.UserRequest;
import com.storix.dto.UserResponse;
import com.storix.service.UserService;
import com.storix.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid  @RequestBody UserRequest user) {
        UserResponse userResponse = userService.createUser(user);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse userResponse = userService.findByEmail(email);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }
}
