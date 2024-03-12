package com.example.ssetest.service;

import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author sjChoi
 * @since 3/12/24
 */
@Service
@RequiredArgsConstructor
public class ChattingRoomParticipantsService {

    private final ChattingRoomParticipantsRepository chattingRoomParticipantsRepository;

    @Transactional
    public ChattingRoomParticipants updateParticipants(ChattingRoomParticipants chattingRoomParticipants) {
        ChattingRoomParticipants detailParticipants = chattingRoomParticipantsRepository.findByChattingRoomUidAndParticipantsUid(chattingRoomParticipants.getChattingRoomUid(), chattingRoomParticipants.getParticipantsUid());
        if (detailParticipants != null && detailParticipants.getConnectYn().equals("Y")) {
            detailParticipants = detailParticipants.toBuilder().connectYn("N").build();
//            chattingRoomParticipants = chattingRoomParticipants.toBuilder().unreadMessage(detailParticipants.getUnreadMessage()).connectYn("N").build();
            return chattingRoomParticipantsRepository.save(detailParticipants);
        }
        return null;
    }
}
