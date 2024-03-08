package com.example.ssetest.service;

import com.example.ssetest.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * @author sjChoi
 * @since 2/6/24
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {


    @KafkaListener(topics = "gw-notification-topic", groupId = "foo")
    public void consumer(@Payload NotificationMessage message) {
        log.info(String.format("Consumed message: %s", message.getMessage()));
    }

}
