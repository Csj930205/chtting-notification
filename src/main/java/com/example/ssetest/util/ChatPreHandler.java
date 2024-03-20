package com.example.ssetest.util;

import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.domain.Member;
import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Component
@RequiredArgsConstructor
public class ChatPreHandler implements ChannelInterceptor {

    private final ChattingRoomParticipantsRepository chattingRoomParticipantsRepository;

    public static Map<String, List<String>> chattingUserList = new ConcurrentHashMap<>();

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            StompCommand command = accessor.getCommand();
            Authentication authentication = (Authentication) accessor.getHeader("simpUser");
            accessor.setUser(authentication);
            Member member = (Member) authentication.getPrincipal();
            List<String> userList = chattingUserList.get("userList");

            if (StompCommand.CONNECT.equals(command) || StompCommand.SEND.equals(command) || StompCommand.MESSAGE.equals(command)) {
                return message;
            }

            if (StompCommand.SUBSCRIBE.equals(command)) {
                if (userList == null || userList.size() == 0) {
                    userList = new ArrayList<>();
                }
                if (!userList.contains(member.getUsername())) {
                    userList.add(member.getUsername());
                    chattingUserList.put("userList", userList);
                    System.out.println("연결 후 구독자 수: " + userList.size());
                }
                return message;
            }

            if (StompCommand.DISCONNECT.equals(command)) {
                if (userList != null && userList.contains(member.getUsername())) {
                    userList.remove(member.getUsername());
                    System.out.println("연결끊긴 후 구독자수: " + userList.size());
                    List<ChattingRoomParticipants> chattingRoomParticipantsList = chattingRoomParticipantsRepository.findByParticipantsUid(member.getUsername());
                    if (chattingRoomParticipantsList != null && chattingRoomParticipantsList.size() > 0) {
                        for (ChattingRoomParticipants participants : chattingRoomParticipantsList) {
                            if (participants.getConnectYn().equals("Y")) {
                                ChattingRoomParticipants chattingRoomParticipants = ChattingRoomParticipants.builder()
                                        .uid(participants.getUid())
                                        .chattingRoomUid(participants.getChattingRoomUid())
                                        .participantsUid(participants.getParticipantsUid())
                                        .unreadMessage(participants.getUnreadMessage())
                                        .connectYn("N")
                                        .build();
                                chattingRoomParticipantsRepository.save(chattingRoomParticipants);
                            }
                        }
                    }
                }
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

}
