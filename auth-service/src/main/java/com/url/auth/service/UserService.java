package com.url.auth.service;

import com.url.auth.dto.LoginRequest;
import com.url.auth.models.User;
import com.url.auth.repository.UserRepository;
import com.url.auth.security.jwt.JwtAuthenticationResponse;
import com.url.auth.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private PasswordEncoder passwordEncoder;
  private UserRepository userRepository;
  private AuthenticationManager authenticationManager;
  private JwtUtils jwtUtils;

  public void registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
  }

  public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    String jwt = jwtUtils.generateToken(userDetails);
    return new JwtAuthenticationResponse(jwt);
  }

  public User findByUsername(String name) {
    return userRepository
        .findByUsername(name)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username = " + name));
  }
}
