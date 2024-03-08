package com.example.ssetest.dto;

import com.example.ssetest.domain.ChattingRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/29/24
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomRequestDto {

    private List<String> participants;

    private String createdBy;

    public ChattingRoom toEntity() {
        return ChattingRoom.builder()
                .createdBy(createdBy)
                .build();
    }
}
