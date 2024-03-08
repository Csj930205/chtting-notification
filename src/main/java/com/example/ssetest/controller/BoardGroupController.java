package com.example.ssetest.controller;

import com.example.ssetest.domain.BoardGroup;
import com.example.ssetest.service.BoardGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
@RestController
@RequestMapping("/apis/board-group")
@RequiredArgsConstructor
public class BoardGroupController {

    private final BoardGroupService boardGroupService;

    @GetMapping("list")
    public List<BoardGroup> findAllBoardGroupList() {
        List<BoardGroup> findAllBoardGroupList = boardGroupService.findAllBoardGroupList();

        return findAllBoardGroupList;
    }

    @GetMapping("/{name}")
    public BoardGroup findByBoardGroup(@PathVariable(value = "name") String name) {
        BoardGroup boardGroup = boardGroupService.findByBoardGroup(name);
        return boardGroup;
    }

    @PostMapping
    public String saveBoardGroup(@RequestBody BoardGroup boardGroup) {
        int saveBoardGroup = boardGroupService.saveBoardGroup(boardGroup);
        if (saveBoardGroup == 1) {
            return "success";
        } else {
            return "fail";
        }
    }


}
