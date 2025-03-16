package com.url.shortener.controller;

import com.url.shortener.dto.LoginRequest;
import com.url.shortener.dto.RegisterRequest;
import com.url.shortener.models.User;
import com.url.shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

  private UserService userService;

  @PostMapping("/public/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(userService.authenticateUser(loginRequest));
  }

  @PostMapping("/public/register")
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
