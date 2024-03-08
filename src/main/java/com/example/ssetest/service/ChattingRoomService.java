package com.example.ssetest.service;

import com.example.ssetest.domain.ChatMessage;
import com.example.ssetest.domain.ChattingRoom;
import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.dto.ChattingRoomRequestDto;
import com.example.ssetest.dto.ChattingRoomResponseDto;
import com.example.ssetest.repository.ChattingRoomParticipantsRepository;
import com.example.ssetest.repository.ChattingRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sjChoi
 * @since 2/14/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingRoomService {

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomParticipantsRepository chattingRoomParticipantsRepository;

    @Transactional(readOnly = true)
    public List<ChattingRoomResponseDto> chattingRoomList() {
        List<ChattingRoom> chattingRoomList = chattingRoomRepository.findAll();
        List<ChattingRoomResponseDto> chattingRoomResponseDtoList = new ArrayList<>();
        for (ChattingRoom chattingRoom : chattingRoomList) {
            List<ChattingRoomParticipants> participants = chattingRoomParticipantsRepository.findByChattingRoomUid(chattingRoom.getUid());
            List<String> participantsList = new ArrayList<>();

            for (ChattingRoomParticipants chattingRoomParticipants : participants) {
                participantsList.add(chattingRoomParticipants.getParticipantsUid());
            }
            ChattingRoomResponseDto chattingRoomResponseDto = new ChattingRoomResponseDto(chattingRoom, participantsList);
            chattingRoomResponseDtoList.add(chattingRoomResponseDto);
        }
        return chattingRoomResponseDtoList;
    }

    @Transactional
    public ChattingRoom insertChattingRoom(ChattingRoomRequestDto chattingRoomRequestDto) {
        ChattingRoom chattingRoom = chattingRoomRequestDto.toEntity();
        ChattingRoom insertChattingRoom  = chattingRoomRepository.save(chattingRoom);
        ChattingRoomParticipants createdByChattingRoom = ChattingRoomParticipants.builder()
                .chattingRoomUid(insertChattingRoom.getUid())
                .participantsUid(insertChattingRoom.getCreatedBy())
                .build();
        chattingRoomParticipantsRepository.save(createdByChattingRoom);

        for (String participants : chattingRoomRequestDto.getParticipants()) {
            ChattingRoomParticipants chattingRoomParticipants = ChattingRoomParticipants.builder()
                    .chattingRoomUid(insertChattingRoom.getUid())
                    .participantsUid(participants)
                    .build();
            chattingRoomParticipantsRepository.save(chattingRoomParticipants);
        }
        return insertChattingRoom;
    }

}
