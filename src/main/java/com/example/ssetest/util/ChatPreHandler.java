package com.example.ssetest.util;

import com.example.ssetest.controller.ChatMessageController;
import com.example.ssetest.domain.Member;
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

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Component
@RequiredArgsConstructor
public class ChatPreHandler implements ChannelInterceptor {

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            StompCommand command = accessor.getCommand();
            Authentication authentication = (Authentication) accessor.getHeader("simpUser");
            accessor.setUser(authentication);
            Member member = (Member) authentication.getPrincipal();

            if (StompCommand.CONNECT.equals(command) || StompCommand.SEND.equals(command) || StompCommand.MESSAGE.equals(command)) {
                System.out.println("===========================");
                System.out.println("인증객체: " + authentication);
                System.out.println("===========================");

                if (StompCommand.CONNECT.equals(command)) {
                    List<String> userList = ChatMessageController.chattingUserList.get("userList");
                    if (userList == null) {
                        userList = new ArrayList<>();
                        ChatMessageController.chattingUserList.put("userList", userList);
                        userList.add(member.getUsername());
                    }
                    if (!userList.contains(member.getUsername())){
                        userList.add(member.getUsername());
                    }
                }

                List<String> userList = ChatMessageController.chattingUserList.get("userList");
                System.out.println("현재 구독중인 구독자수: " + userList.size());

                return message;
            } else {
                if (StompCommand.SUBSCRIBE.equals(command)) {
                    List<String> userList = ChatMessageController.chattingUserList.get("userList");

                    System.out.println("구독자수: " + userList.size());
                }
                if (StompCommand.DISCONNECT.equals(command)) {
                    List<String> userList = ChatMessageController.chattingUserList.get("userList");
                    if (userList != null) {

                        System.out.println("연결끊긴 후 구독자수: " + userList.size());
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
