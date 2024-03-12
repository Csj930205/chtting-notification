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
import org.springframework.web.bind.annotation.*;
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
        List<String> userList = chattingUserList.get("userList");
        for (ChattingRoomParticipants participants : participantsList) {
            ChattingRoomParticipants detailParticipants = chattingRoomParticipantsRepository.findByChattingRoomUidAndParticipantsUid(participants.getChattingRoomUid(), participants.getParticipantsUid());
            if (userList.contains(participants.getParticipantsUid())) {
                simpMessageSendingOperations.convertAndSend("/chat?userId=" + participants.getParticipantsUid(), chatMessage);
                if (detailParticipants.getConnectYn().equals("N")) {
                    ChattingRoomParticipants chattingRoomParticipants = ChattingRoomParticipants.builder()
                            .uid(detailParticipants.getUid())
                            .chattingRoomUid(detailParticipants.getChattingRoomUid())
                            .connectYn("N")
                            .participantsUid(detailParticipants.getParticipantsUid())
                            .unreadMessage(detailParticipants.getUnreadMessage() + 1)
                            .build();
                    chattingRoomParticipantsRepository.save(chattingRoomParticipants);
                }
            } else {
                ChattingRoomParticipants chattingRoomParticipants = ChattingRoomParticipants.builder()
                        .uid(detailParticipants.getUid())
                        .chattingRoomUid(detailParticipants.getChattingRoomUid())
                        .connectYn("N")
                        .participantsUid(detailParticipants.getParticipantsUid())
                        .unreadMessage(detailParticipants.getUnreadMessage() + 1)
                        .build();
                chattingRoomParticipantsRepository.save(chattingRoomParticipants);
            }
        }
    }

    @GetMapping(value = "/apis/chatting-list/{roomUid}")
    public List<ChatMessage> chattingMessageList(@PathVariable(value = "roomUid") String roomUid,  @RequestParam(required = false) Map<String, Object> paramMap) {
        List<ChatMessage> chatMessagesList = chatMessageService.getAllChatMessage(roomUid, paramMap);
        String userUid = SecurityUtil.getCurrentMember().getUsername();
        ChattingRoomParticipants detailParticipants = chattingRoomParticipantsRepository.findByChattingRoomUidAndParticipantsUid(Long.valueOf(roomUid), userUid);
        ChattingRoomParticipants chattingRoomParticipants = ChattingRoomParticipants.builder()
                .uid(detailParticipants.getUid())
                .chattingRoomUid(detailParticipants.getChattingRoomUid())
                .participantsUid(detailParticipants.getParticipantsUid())
                .build();
        if (detailParticipants.getUnreadMessage() > 0) {
            chattingRoomParticipants = chattingRoomParticipants.toBuilder().unreadMessage(0).build();
        }
        if (detailParticipants.getConnectYn().equals("N")) {
            chattingRoomParticipants = chattingRoomParticipants.toBuilder().connectYn("Y").build();
        } else {
            chattingRoomParticipants = chattingRoomParticipants.toBuilder().connectYn(detailParticipants.getConnectYn()).build();
        }
        chattingRoomParticipantsRepository.save(chattingRoomParticipants);
        return chatMessagesList;
    }

    @Scheduled(fixedRate = 10000)
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
