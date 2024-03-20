package com.example.ssetest.controller;

import com.example.ssetest.domain.NotificationMessage;
import com.example.ssetest.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sjChoi
 * @since 2/2/24
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", " http://localhost:8787"})
public class NotificationController {

    private final NotificationService notificationService;
    public static Map<String, SseEmitter> boardSseEmitters = new ConcurrentHashMap<>();


    @GetMapping(value = "/apis/notifications/subscribe/{username}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable(value = "username") String username, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        SseEmitter sseEmitter = notificationService.subscribe(username, lastEventId);
        return sseEmitter;
    }

    @GetMapping(value = "/apis/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> seeEvents() {
        String uuid = String.valueOf(UUID.randomUUID());
        return Flux.interval(Duration.ofSeconds(1))
                .map(seq -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(uuid))
                        .event("see-event")
                        .data("Cuttne Time: " + LocalTime.now())
                        .build());
    }

    @GetMapping(value = "/apis/list")
    public List<NotificationMessage> notificationMessageList(@RequestParam(required = false) Map<String, Object> paramMap) throws IOException {
        List<NotificationMessage> notificationMessageList = notificationService.getAllNotification(paramMap);
        return notificationMessageList;
    }

}
