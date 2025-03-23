package com.url.analytics.service;

import com.url.analytics.models.User;
import com.url.analytics.repository.UserRepository;
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
