package com.url.shortener.service;

import com.url.shortener.dto.ClickEventDTO;
import com.url.shortener.dto.UrlMappingDTO;
import com.url.shortener.mappers.UrlMappingToUrlMappingDTO;
import com.url.shortener.models.ClickEvent;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import com.url.shortener.repository.ClickEventRepository;
import com.url.shortener.repository.UrlMappingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UrlMappingService {

  private UrlMappingRepository urlMappingRepository;
  private ClickEventRepository clickEventRepository;

  public UrlMappingDTO createShortUrl(String originalUrl, User user) {
    String shortUrl = generateShortUrl();
    UrlMapping urlMapping = new UrlMapping();
    LocalDateTime now = LocalDateTime.now();
    urlMapping.setOriginalUrl(originalUrl);
    urlMapping.setShortUrl(shortUrl);
    urlMapping.setUser(user);
    urlMapping.setCreatedDate(now);
    urlMapping.setUpdatedDate(now);
    UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);

    return UrlMappingToUrlMappingDTO.INSTANCE.map(savedUrlMapping);
  }

  private String generateShortUrl() {
    Random random = new Random();
    String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder shortUrl = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      shortUrl.append(alphabet.charAt(random.nextInt(alphabet.length())));
    }
    return shortUrl.toString();
  }

  public List<UrlMappingDTO> getUrlsByUser(User user) {
    return urlMappingRepository.findByUser(user).stream()
        .map(UrlMappingToUrlMappingDTO.INSTANCE::map)
        .toList();
  }

  public List<ClickEventDTO> getClickEventsByDate(
      String shortUrl, LocalDateTime start, LocalDateTime end) {
    UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
    if (Objects.nonNull(urlMapping)) {
      return clickEventRepository
          .findByUrlMappingAndClickDateBetween(urlMapping, start, end)
          .stream()
          .collect(
              Collectors.groupingBy(
                  click -> click.getClickDate().toLocalDate(), Collectors.counting()))
          .entrySet()
          .stream()
          .map(
              entry -> {
                ClickEventDTO clickEventDTO = new ClickEventDTO();
                clickEventDTO.setClickDate(entry.getKey());
                clickEventDTO.setCount(entry.getValue());
                return clickEventDTO;
              })
          .toList();
    }
    return List.of();
  }

  public Map<LocalDate, Long> getTotalClicksByUser(User user, LocalDate start, LocalDate end) {
    List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
    return clickEventRepository
        .findByUrlMappingInAndClickDateBetween(
            urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
        .stream()
        .collect(
            Collectors.groupingBy(
                click -> click.getClickDate().toLocalDate(), Collectors.counting()));
  }

  public UrlMapping getOriginalUrl(String shortUrl) {
    UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
    if (Objects.nonNull(urlMapping)) {
      LocalDateTime now = LocalDateTime.now();

      urlMapping.setClickCount(urlMapping.getClickCount() + 1);
      urlMapping.setUpdatedDate(now);
      urlMappingRepository.save(urlMapping);

      // Record click event
      ClickEvent clickEvent = new ClickEvent();
      clickEvent.setClickDate(now);
      clickEvent.setUrlMapping(urlMapping);
      clickEventRepository.save(clickEvent);
    }
    return urlMapping;
  }
}
