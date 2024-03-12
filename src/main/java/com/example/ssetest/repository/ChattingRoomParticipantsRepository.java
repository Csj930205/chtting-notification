package com.example.ssetest.repository;

import com.example.ssetest.domain.ChattingRoomParticipants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/29/24
 */
public interface ChattingRoomParticipantsRepository extends JpaRepository<ChattingRoomParticipants, Long> {

    List<ChattingRoomParticipants> findByChattingRoomUid(Long chattingRoomUid);

    List<ChattingRoomParticipants> findByParticipantsUid(String userUid);

    ChattingRoomParticipants findByChattingRoomUidAndParticipantsUid(Long chattingRoomUid, String participantsUid);

}
