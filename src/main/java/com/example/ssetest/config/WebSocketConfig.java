package com.example.ssetest.config;

import com.example.ssetest.controller.ChatMessageController;
import com.example.ssetest.controller.NotificationController;
import com.example.ssetest.util.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sjChoi
 * @since 2/14/24
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private String origins = "http://localhost:3000";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOrigins("*");

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @EventListener
    public void connectWebSocket(SessionConnectEvent sessionConnectEvent) {
        String sessionId = sessionConnectEvent.getMessage().getHeaders().get("simpSessionId").toString();
        List<String> sessionIdList = ChatMessageController.chattingUserList.get("sessionId");
        if (sessionIdList == null) {
            sessionIdList = new ArrayList<>();
            ChatMessageController.chattingUserList.put("sessionId", sessionIdList);
        }
        sessionIdList.add(sessionId);

        System.out.println("연결");
    }

    @EventListener
    public void disConnectWebSocket(SessionDisconnectEvent sessionDisconnectEvent) {
        String sessionId = sessionDisconnectEvent.getSessionId();
        List<String> sessionIdList = ChatMessageController.chattingUserList.get("sessionId");
        sessionIdList.remove(sessionId);
        System.out.println("연결 끊김");
    }

}
