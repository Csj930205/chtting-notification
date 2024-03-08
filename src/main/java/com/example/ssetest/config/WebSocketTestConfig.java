//package com.example.ssetest.config;
//
//import com.example.ssetest.util.WebSocketHandler;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
///**
// * @author sjChoi
// * @since 3/7/24
// */
//@Configuration
//@EnableWebSocket
//@RequiredArgsConstructor
//public class WebSocketTestConfig implements WebSocketConfigurer {
//
//    private final WebSocketHandler webSocketHandler;
//
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(webSocketHandler, "/chat").setAllowedOrigins("*");
//    }
//}
