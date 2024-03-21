package com.example.ssetest.service;

import com.example.ssetest.controller.NotificationController;
import com.example.ssetest.domain.*;
import com.example.ssetest.util.ChatPreHandler;
import com.example.ssetest.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

/**
 * @author sjChoi
 * @since 2/2/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ElasticsearchOperations elasticsearchOperations;

    private final BoardGroupService boardGroupService;

    private final BoardGroupUserService boardGroupUserService;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    private final MemberService memberService;

    private final UnreadNotificationService unreadNotificationService;

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    /**
     * 웹소켓
     * @param boardArticle
     */
    public void notificationMessage(BoardArticle boardArticle) {
        BoardGroup boardGroup = boardGroupService.detailBoardGroup(boardArticle.getBoardGroupUid());
        String message = boardArticle.getCreatedBy() + "님이 " + boardGroup.getName() + " 그룹에 게시글을 작성하였습니다.";
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .parentUid(boardArticle.getUid())
                .type("boardArticle")
                .message(message)
                .memberUid(boardArticle.getCreatedBy())
                .build();
        List<Member> memberList = memberService.memberList();

        if (boardGroup.getMandatory().equals("Y")) {
            for (Map.Entry<String, List<String>> entry : ChatPreHandler.chattingUserList.entrySet()) {
                for (String userList : entry.getValue()) {
                    simpMessageSendingOperations.convertAndSend("/notification?userId=" + userList, notificationMessage);
                }
            }
            for (Member member : memberList) {
                if (!ChatPreHandler.chattingUserList.get("userList").contains(member.getUsername())) {
                    UnreadNotification unreadNotification = UnreadNotification.builder()
                            .memberUid(member.getUsername())
                            .parentUid(boardArticle.getUid())
                            .message(message)
                            .build();
                    unreadNotificationService.insertUnreadNotification(unreadNotification);
                }
            }
        } else {
            List<BoardGroupUser> boardGroupUserList = boardGroupUserService.findByBoardGroupUid(boardArticle.getBoardGroupUid());
            for (BoardGroupUser boardGroupUser : boardGroupUserList) {
                try {
                    for (Map.Entry<String, List<String>> entry : ChatPreHandler.chattingUserList.entrySet()) {
                        for (String userList : entry.getValue()) {
                            simpMessageSendingOperations.convertAndSend("/notification?userId=" + userList, notificationMessage);
                        }
                    }
                    if (!ChatPreHandler.chattingUserList.get("userList").contains(boardGroupUser.getBoardGroupUsername())) {
                        UnreadNotification unreadNotification = UnreadNotification.builder()
                                .memberUid(boardGroupUser.getBoardGroupUsername())
                                .parentUid(boardArticle.getUid())
                                .message(message)
                                .build();
                        unreadNotificationService.insertUnreadNotification(unreadNotification);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            ProducerRecord<String, NotificationMessage> record = new ProducerRecord<>("gw-notification-topic",0,  boardArticle.getCreatedBy(), notificationMessage);
            kafkaTemplate.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SSE
     * @param username
     * @param lastEventId
     * @return
     */
    public SseEmitter subscribe(String username, String lastEventId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("GOOD"));
            log.info("success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String getUsername = SecurityUtil.getCurrentMember().getUsername();
        NotificationController.boardSseEmitters.put(getUsername, sseEmitter);

        sseEmitter.onCompletion(() -> NotificationController.boardSseEmitters.remove(username));	// sseEmitter 연결이 완료될 경우
        sseEmitter.onTimeout(() -> NotificationController.boardSseEmitters.remove(username));		// sseEmitter 연결에 타임아웃이 발생할 경우
        sseEmitter.onError((e) -> NotificationController.boardSseEmitters.remove(username));		// sseEmitter 연결에 오류가 발생할 경우

        if(!lastEventId.isEmpty()) { //client가 미수신한 event가 존재하는 경우 이를 전송하여 유실 예방
            log.info("유실된 데이터가 존재");
        }

        return sseEmitter;
    }

    public void notificationBoard(BoardArticle boardArticle) {
        BoardGroup boardGroup = boardGroupService.detailBoardGroup(boardArticle.getBoardGroupUid());
        String message = boardArticle.getCreatedBy() + "님이 " + boardGroup.getName() + " 그룹에 게시글을 작성하였습니다.";
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .message(message)
                .memberUid(boardArticle.getCreatedBy())
                .build();
        List<Member> memberList = memberService.memberList();

        if (boardGroup.getMandatory().equals("Y")) {
            for (Map.Entry<String, SseEmitter> entry : NotificationController.boardSseEmitters.entrySet()) {
                SseEmitter sseEmitter = entry.getValue();
                try {
                    if (sseEmitter != null) {
                        sseEmitter.send(SseEmitter.event().id(String.valueOf(boardArticle.getUid())).name("boardArticle").data(message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Member member : memberList) {
                if (!NotificationController.boardSseEmitters.containsKey(member.getUsername())) {
                    UnreadNotification unreadNotification = UnreadNotification.builder()
                            .memberUid(member.getUsername())
                            .parentUid(boardArticle.getUid())
                            .message(message)
                            .build();
                    unreadNotificationService.insertUnreadNotification(unreadNotification);
                }
            }
        } else {
            List<BoardGroupUser> boardGroupUserList = boardGroupUserService.findByBoardGroupUid(boardArticle.getBoardGroupUid());
            for (BoardGroupUser boardGroupUser : boardGroupUserList) {
                try {
                    SseEmitter sseEmitter = NotificationController.boardSseEmitters.get(boardGroupUser.getBoardGroupUsername());
                    if (sseEmitter != null) {
                        sseEmitter.send(SseEmitter.event().id(String.valueOf(boardArticle.getUid())).name("boardArticle").data(message));
                    } else {
                        UnreadNotification unreadNotification = UnreadNotification.builder()
                                .memberUid(boardGroupUser.getBoardGroupUsername())
                                .parentUid(boardArticle.getUid())
                                .message(message)
                                .build();
                        unreadNotificationService.insertUnreadNotification(unreadNotification);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            ProducerRecord<String, NotificationMessage> record = new ProducerRecord<>("gw-notification-topic",0,  boardArticle.getCreatedBy(), notificationMessage);
            kafkaTemplate.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 엘라스틱서치에 저장되어있는 모든정보를 조회 후 리턴
     * @return
     * @throws IOException
     */
    public List<NotificationMessage> getAllNotification(Map<String, Object> paramMap) throws IOException {
        List<NotificationMessage> notificationMessageList = new ArrayList<>();
        Criteria criteria;
        if (paramMap.containsKey("title")) {
            String title = paramMap.get("title").toString();
            criteria = new Criteria("message").contains(title);
        } else {
            criteria = new Criteria();
        }

        CriteriaQuery query;
        if (paramMap.containsKey("size") ) {
            int page = Integer.parseInt(paramMap.get("page").toString());
            int size = Integer.parseInt(paramMap.get("size").toString());
            int from = page - 1; // Elasticseasrch 의 검색쿼리에서는 0부터 시작해서 페이지를 계산함으로 -1 을 넣어줘야함
            Pageable pageable = PageRequest.of(from, size);
            query = new CriteriaQuery(criteria).setPageable(pageable);
        } else {
            query = new CriteriaQuery(criteria);
        }

        SearchHits<NotificationMessage> searchHits = elasticsearchOperations.search(query, NotificationMessage.class);
        searchHits.forEach(hit -> notificationMessageList.add(hit.getContent()));
        System.out.println(notificationMessageList.size());
        return notificationMessageList;
    }
}
