package com.example.ssetest.service;

import com.example.ssetest.domain.UnreadNotification;
import com.example.ssetest.repository.UnreadNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjChoi
 * @since 3/19/24
 */
@Service
@RequiredArgsConstructor
public class UnreadNotificationService {

    private final UnreadNotificationRepository unreadNotificationRepository;

    @Transactional
    public List<UnreadNotification> unreadNotificationList(String memberUid) {
        List<UnreadNotification> unreadNotificationList = unreadNotificationRepository.findAllByMemberUid(memberUid);
        return unreadNotificationList;
    }

    @Transactional
    public void insertUnreadNotification(UnreadNotification unreadNotification) {
        unreadNotificationRepository.save(unreadNotification);
    }

    @Transactional
    public List<UnreadNotification> deleteUnreadNotification(String memberUid) {
        List<UnreadNotification> unreadNotificationList = unreadNotificationList(memberUid);
        unreadNotificationRepository.deleteByMemberUid(memberUid);
        return unreadNotificationList;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> countUnreadNotification(String memberUid) {
        Map<String, Object> result = new HashMap<>();
        List<UnreadNotification> unreadNotificationList = unreadNotificationList(memberUid);
        int count = unreadNotificationRepository.countByMemberUid(memberUid);
        result.put("list", unreadNotificationList);
        result.put("count", count);
        return result;
    }
}
