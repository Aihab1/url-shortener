package com.url.redirect.kafka;

import com.url.redirect.models.UrlMapping;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class RedirectProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectProducer.class);

    private final NewTopic topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public RedirectProducer(KafkaTemplate<String, String> kafkaTemplate, NewTopic topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(String id) {
        LOGGER.info("Producing message -> UrlMapping id: {}", id);

        Message<String> message = MessageBuilder
                .withPayload(id)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
    }
}
