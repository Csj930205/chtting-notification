package com.example.ssetest.service;

import com.example.ssetest.domain.UnreadNotification;
import com.example.ssetest.repository.UnreadNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sjChoi
 * @since 3/19/24
 */
@Service
@RequiredArgsConstructor
public class UnreadNotificationService {

    private final UnreadNotificationRepository unreadNotificationRepository;

    @Transactional
    public void insertUnreadNotification(UnreadNotification unreadNotification) {
        unreadNotificationRepository.save(unreadNotification);
    }

    @Transactional
    public void deleteUnreadNotification(String memberUid) {
        unreadNotificationRepository.deleteByMemberUid(memberUid);
    }

    @Transactional(readOnly = true)
    public int countUnreadNotification(String memberUid) {
        int count = unreadNotificationRepository.countByMemberUid(memberUid);
        return count;
    }
}
