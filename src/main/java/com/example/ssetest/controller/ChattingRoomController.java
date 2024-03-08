package com.example.ssetest.controller;

import com.example.ssetest.domain.ChattingRoom;
import com.example.ssetest.dto.ChattingRoomRequestDto;
import com.example.ssetest.dto.ChattingRoomResponseDto;
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

    @GetMapping
    public Map<String, Object> selectChattingRoomList() {
        Map<String, Object> result = new HashMap<>();
        List<ChattingRoomResponseDto> chattingRoomList = chattingRoomService.chattingRoomList();
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

}
