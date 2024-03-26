package com.example.ssetest.controller;

import com.example.ssetest.domain.ChatMessage;
import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
import com.example.ssetest.service.ChatMessageService;
import com.example.ssetest.util.AttachUtil;
import com.example.ssetest.util.ChatPreHandler;
import com.example.ssetest.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

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

    private final AttachUtil attachUtil;

    @MessageMapping("/chat/send-message")
    public void chatMessage (@Payload ChatMessage chatMessage) {
        ProducerRecord<String, ChatMessage> record = new ProducerRecord<>("gw-chat-topic", 0, chatMessage.getRoomUid(), chatMessage);
        kafkaTemplate.send(record);
        List<ChattingRoomParticipants> participantsList = chattingRoomParticipantsRepository.findByChattingRoomUid(Long.valueOf(chatMessage.getRoomUid()));
        List<String> userList = ChatPreHandler.chattingUserList.get("userList");
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

    @MessageMapping("/notification/heartbeat")
    public void heartBeat(String heartBeat) {
        List<String> userList = ChatPreHandler.chattingUserList.get("userList");
        String pong= null;
        if (heartBeat.equals("PING")) {
            pong = "PONG";
        }
        if (userList != null && userList.size() > 0) {
            for (Map.Entry<String, List<String>> entry : ChatPreHandler.chattingUserList.entrySet()) {
                for (String user : entry.getValue()) {
                    simpMessageSendingOperations.convertAndSend("/notification?userId=" + user, pong);
                }
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
        List<String> chattingUserList = ChatPreHandler.chattingUserList.get("userList");
        if (chattingUserList != null && chattingUserList.size() > 0) {
            System.out.println("현재 접속자수: " + chattingUserList.size());
            for (Map.Entry<String, List<String>> entry : ChatPreHandler.chattingUserList.entrySet()) {
                for (String userList : entry.getValue()) {
                    simpMessageSendingOperations.convertAndSend("/notification?userId=" + userList, ChatPreHandler.chattingUserList);
                }
            }
        }
    }

}
