package com.example.ssetest.dto;

import com.example.ssetest.domain.ChattingRoomParticipants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author sjChoi
 * @since 2/29/24
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomParticipantsResponseDto {

    private Long uid;

    private Long chattingRoomUid;

    private String participantsUid;

    private Date joinedDate;

    public ChattingRoomParticipantsResponseDto(ChattingRoomParticipants chattingRoomParticipants) {
        this.uid = chattingRoomParticipants.getUid();
        this.chattingRoomUid = chattingRoomParticipants.getChattingRoomUid();
        this.participantsUid = chattingRoomParticipants.getParticipantsUid();
        this.joinedDate = chattingRoomParticipants.getJoinedDate();
    }
}
