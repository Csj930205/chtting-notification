package com.example.ssetest.controller;

import com.example.ssetest.domain.ChatMessage;
import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.domain.Member;
import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
import com.example.ssetest.repository.ChattingRoomRepository;
import com.example.ssetest.service.ChatMessageService;
import com.example.ssetest.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sjChoi
 * @since 2/15/24
 */
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final KafkaTemplate<String, ChatMessage> kafkaTemplate;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private final ChatMessageService chatMessageService;

    private final ChattingRoomParticipantsRepository chattingRoomParticipantsRepository;

    public static Map<String, List<String>> chattingUserList = new ConcurrentHashMap<>();

    @MessageMapping("/chat/send-message")
    public void chatMessage (@Payload ChatMessage chatMessage, StompHeaderAccessor accessor) {
        ProducerRecord<String, ChatMessage> record = new ProducerRecord<>("gw-chat-topic", 0, chatMessage.getRoomUid(), chatMessage);
        kafkaTemplate.send(record);
        List<ChattingRoomParticipants> participantsList = chattingRoomParticipantsRepository.findByChattingRoomUid(Long.valueOf(chatMessage.getRoomUid()));

        for (ChattingRoomParticipants participants : participantsList) {
            simpMessageSendingOperations.convertAndSend("/chat?userId=" + participants.getParticipantsUid(), chatMessage);
        }
    }

    @GetMapping(value = "/apis/chatting-list/{roomUid}")
    public List<ChatMessage> chattingMessageList(@PathVariable(value = "roomUid") String roomUid,  @RequestParam(required = false) Map<String, Object> paramMap) {
        List<ChatMessage> chatMessagesList = chatMessageService.getAllChatMessage(roomUid, paramMap);
        return chatMessagesList;
    }

    @Scheduled(fixedRate = 100000)
    public void pushWebSocket() {
        System.out.println("1분마다 실행되야함");
        if (chattingUserList.get("userList") != null) {
            for (Map.Entry<String, List<String>> entry : chattingUserList.entrySet()) {
                for (String userList : entry.getValue()) {
                    simpMessageSendingOperations.convertAndSend("/chat?userId=" + userList, chattingUserList);
                }
            }
        }
    }

}
