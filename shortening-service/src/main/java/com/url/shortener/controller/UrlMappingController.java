package com.url.shortener.controller;

import com.url.shortener.dto.ClickEventDTO;
import com.url.shortener.dto.UrlMappingDTO;
import com.url.shortener.models.User;
import com.url.shortener.service.UrlMappingService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.url.shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UrlMappingController {

  private UrlMappingService urlMappingService;
  private UserService userService;

  @PostMapping("/shorten")
  public ResponseEntity<UrlMappingDTO> createShortUrl(
      @RequestBody Map<String, String> request, Principal principal) {
    String originalUrl = request.get("originalUrl");
    User user = userService.findByUsername(principal.getName());
    UrlMappingDTO urlMappingDTO = urlMappingService.createShortUrl(originalUrl, user);
    return ResponseEntity.ok(urlMappingDTO);
  }

  @GetMapping("/urls")
  public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal) {
    User user = userService.findByUsername(principal.getName());
    List<UrlMappingDTO> urlMappingDTOs = urlMappingService.getUrlsByUser(user);
    return ResponseEntity.ok(urlMappingDTOs);
  }

  @GetMapping("/analytics/{shortUrl}")
  public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(
      @PathVariable String shortUrl, @RequestParam String startDate, @RequestParam String endDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime start = LocalDateTime.parse(startDate, formatter);
    LocalDateTime end = LocalDateTime.parse(endDate, formatter);
    List<ClickEventDTO> clickEventDTOs =
        urlMappingService.getClickEventsByDate(shortUrl, start, end);
    return ResponseEntity.ok(clickEventDTOs);
  }

  @GetMapping("/analytics/totalClicks")
  public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(
      Principal principal, @RequestParam String startDate, @RequestParam String endDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    LocalDate start = LocalDate.parse(startDate, formatter);
    LocalDate end = LocalDate.parse(endDate, formatter);
    User user = userService.findByUsername(principal.getName());
    Map<LocalDate, Long> totalClicks = urlMappingService.getTotalClicksByUser(user, start, end);
    return ResponseEntity.ok(totalClicks);
  }
}
