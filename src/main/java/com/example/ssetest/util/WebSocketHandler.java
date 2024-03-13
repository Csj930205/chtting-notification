//package com.example.ssetest.util;
//
//import com.example.ssetest.domain.ChatMessage;
//import com.example.ssetest.domain.ChattingRoom;
//import com.example.ssetest.domain.ChattingRoomParticipants;
//import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
//import com.example.ssetest.repository.ChattingRoomRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author sjChoi
// * @since 3/6/24
// */
//@Component
//@RequiredArgsConstructor
//public class WebSocketHandler extends TextWebSocketHandler {
//
//    private final Map<String, List<WebSocketSession>> userList = new ConcurrentHashMap<>();
//
//    private final ObjectMapper objectMapper;
//
//    private final ChattingRoomParticipantsRepository chattingRoomParticipantsRepository;
//
//    private final ChattingRoomRepository chattingRoomRepository;
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        String userId = session.getUri().getQuery().split("=")[1];
//        userList.computeIfAbsent(userId, key -> new ArrayList<>()).add(session);
//        System.out.println("연결");
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        String userId = session.getUri().getQuery().split("=")[1];
//        userList.computeIfPresent(userId, (key, sessions) -> {
//            sessions.remove(session);
//            return sessions.isEmpty() ? null : sessions;
//        });
//        System.out.println("연결 끊김");
//    }
//
//
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
//        List<ChattingRoomParticipants> participantsList = chattingRoomParticipantsRepository.findByChattingRoomUid(Long.valueOf(chatMessage.getRoomUid()));
//
//        for (ChattingRoomParticipants participants : participantsList) {
//            String userId = participants.getParticipantsUid();
//            List<WebSocketSession> sessions = userList.get(userId);
//            if (sessions != null) {
//                for (WebSocketSession receiverSession : sessions) {
//                    receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
//                }
//            }
//        }
//    }
//}
