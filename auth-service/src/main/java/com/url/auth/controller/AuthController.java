package com.url.auth.controller;

import com.url.auth.dto.LoginRequest;
import com.url.auth.dto.RegisterRequest;
import com.url.auth.models.User;
import com.url.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {

  private UserService userService;

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(userService.authenticateUser(loginRequest));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
    User user =
        User.builder()
            .username(registerRequest.getUsername())
            .password(registerRequest.getPassword())
            .email(registerRequest.getEmail())
            .role("ROLE_USER")
            .build();
    userService.registerUser(user);
    return ResponseEntity.ok("User registered successfully");
  }
}
