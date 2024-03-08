package com.example.ssetest.controller;

import com.example.ssetest.domain.ChatMessage;
import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
import com.example.ssetest.repository.ChattingRoomRepository;
import com.example.ssetest.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public void chatMessage (@Payload ChatMessage chatMessage) {
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

//    @Scheduled(fixedRate = 10000)
//    public void pushWebSocket() {
//        System.out.println("10초마다 실행되야함");
//        for (Map.Entry<String, List<String>> entry : chattingUserList.entrySet()) {
//            for (String sessionId : entry.getValue()) {
//                simpMessageSendingOperations.convertAndSend("/chat?userId=" + sessionId, chattingUserList);
//            }
//        }
//    }


}
