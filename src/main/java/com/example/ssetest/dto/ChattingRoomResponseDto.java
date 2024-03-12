package com.example.ssetest.dto;

import com.example.ssetest.domain.ChattingRoom;
import com.example.ssetest.domain.ChattingRoomParticipants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author sjChoi
 * @since 2/29/24
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomResponseDto {

    private Long uid;

    private List<ChattingRoomParticipants> participants;

    private String createdBy;

    private Date createdDate;

    public ChattingRoomResponseDto(ChattingRoom chattingRoom, List<ChattingRoomParticipants> participants) {
        this.uid = chattingRoom.getUid();
        this.participants = participants;
        this.createdBy = chattingRoom.getCreatedBy();
        this.createdDate = chattingRoom.getCreatedDate();
    }

}
