package com.example.ssetest.controller;

import com.example.ssetest.domain.BoardGroupUser;
import com.example.ssetest.service.BoardGroupUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
@RestController
@RequestMapping("/apis/board-group-user")
@RequiredArgsConstructor
public class BoardGroupUserController {

    private final BoardGroupUserService boardGroupUserService;

    @GetMapping("{boardGroupUid}")
    public List<BoardGroupUser> findByBoardGroupUid(@PathVariable(value = "boardGroupUid") Long boardGroupUid) {
        List<BoardGroupUser> boardGroupUserList = boardGroupUserService.findByBoardGroupUid(boardGroupUid);
        return boardGroupUserList;
    }

    @GetMapping("{boardGroupUsername}")
    public List<BoardGroupUser> findByUsername(@PathVariable(value = "boardGroupUsername") String boardGroupUsername) {
        List<BoardGroupUser> boardGroupUserList = boardGroupUserService.findByUsername(boardGroupUsername);
        return boardGroupUserList;
    }

    @PostMapping
    public String saveBoardGroupUser(@RequestBody BoardGroupUser boardGroupUser) {
        int saveBoardGroupUser = boardGroupUserService.saveBoardGroupUser(boardGroupUser);
        if (saveBoardGroupUser == 1) {
            return "success";
        } else {
            return "fail";
        }
    }
}
