package com.example.ssetest.controller;

import com.example.ssetest.domain.ChattingRoom;
import com.example.ssetest.domain.ChattingRoomParticipants;
import com.example.ssetest.dto.ChattingRoomRequestDto;
import com.example.ssetest.dto.ChattingRoomResponseDto;
import com.example.ssetest.service.ChattingRoomParticipantsService;
import com.example.ssetest.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjChoi
 * @since 2/14/24
 */
@RestController
@RequestMapping("/apis/chatting-room")
@RequiredArgsConstructor
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;

    private final ChattingRoomParticipantsService chattingRoomParticipantsService;

    @GetMapping("my-list")
    public Map<String, Object> selectChattingRoomMyList() {
        Map<String, Object> result = new HashMap<>();
        List<ChattingRoomResponseDto> chattingRoomList = chattingRoomService.myChattingRoomList();
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());
        result.put("chattingRoomList", chattingRoomList);
        return result;
    }

    @PostMapping
    public Map<String, Object> insertChattingRoom(@RequestBody ChattingRoomRequestDto chattingRoomRequestDto) {
        Map<String, Object> result = new HashMap<>();
        ChattingRoom insertChattingRoom = chattingRoomService.insertChattingRoom(chattingRoomRequestDto);
        if (insertChattingRoom != null) {
            result.put("result", "success");
            result.put("code", HttpStatus.OK.value());
            result.put("chattingRoom", insertChattingRoom);
        } else {
            result.put("result", "fail");
            result.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return result;
    }

    @PutMapping("leave")
    public Map<String, Object> updateChattingRoomParticipant(@RequestBody ChattingRoomParticipants chattingRoomParticipants) {
        Map<String, Object> result = new HashMap<>();
        ChattingRoomParticipants updateChattingRoomParticipant = chattingRoomParticipantsService.updateParticipants(chattingRoomParticipants);
        if (updateChattingRoomParticipant != null) {
            result.put("result", "success");
            result.put("code", HttpStatus.OK.value());
        } else {
            result.put("result", "fail");
            result.put("code", HttpStatus.NOT_FOUND.value());
        }
        return result;
    }

}
