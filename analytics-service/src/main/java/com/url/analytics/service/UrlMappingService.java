package com.url.analytics.service;

import com.url.analytics.dto.ClickEventDTO;
import com.url.analytics.dto.UrlMappingDTO;
import com.url.analytics.mappers.UrlMappingToUrlMappingDTO;
import com.url.analytics.models.UrlMapping;
import com.url.analytics.models.User;
import com.url.analytics.repository.ClickEventRepository;
import com.url.analytics.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private UrlMappingRepository urlMappingRepository;
    private ClickEventRepository clickEventRepository;

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
}
