package com.example.ssetest.repository;

import com.example.ssetest.domain.UnreadNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sjChoi
 * @since 3/19/24
 */
@Repository
public interface UnreadNotificationRepository extends JpaRepository<UnreadNotification, Long> {

    List<UnreadNotification> findAllByMemberUid(String memberUid);

    int countByMemberUid(String memberUid);

    void deleteByMemberUid(String memberUid);
}
