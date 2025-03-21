package com.url.shortener.service;

import com.url.shortener.models.User;
import com.url.shortener.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private UserRepository userRepository;

  public User findByUsername(String name) {
    return userRepository
        .findByUsername(name)
        .orElseThrow(() -> new RuntimeException("User not found with username = " + name));
  }
}
