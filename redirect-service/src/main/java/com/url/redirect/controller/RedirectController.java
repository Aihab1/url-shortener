package com.url.redirect.controller;

import com.url.redirect.models.UrlMapping;
import com.url.redirect.service.UrlMappingService;
import java.util.Objects;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RedirectController {

  private UrlMappingService urlMappingService;

  @GetMapping("/{shortUrl}")
  public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
    UrlMapping urlMapping = urlMappingService.getOriginalUrl(shortUrl);
    if (Objects.nonNull(urlMapping)) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("Location", urlMapping.getOriginalUrl());
      return ResponseEntity.status(302).headers(httpHeaders).build();
    }
    return ResponseEntity.notFound().build();
  }
}
