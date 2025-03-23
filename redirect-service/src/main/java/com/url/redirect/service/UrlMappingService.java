package com.url.redirect.service;

import com.url.redirect.kafka.RedirectProducer;
import com.url.redirect.models.UrlMapping;
import com.url.redirect.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private UrlMappingRepository urlMappingRepository;
    private RedirectProducer redirectProducer;

    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (Objects.nonNull(urlMapping)) {
            redirectProducer.sendMessage(urlMapping.getId().toString());
//            LocalDateTime now = LocalDateTime.now();
//
//            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
//            urlMapping.setUpdatedDate(now);
//            urlMappingRepository.save(urlMapping);
//
//            // Record click event
//            ClickEvent clickEvent = new ClickEvent();
//            clickEvent.setClickDate(now);
//            clickEvent.setUrlMapping(urlMapping);
//            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }
}
