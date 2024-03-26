package com.example.ssetest.controller;

import com.example.ssetest.domain.BoardArticle;
import com.example.ssetest.service.BoardArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/20/24
 */
@RestController
@RequestMapping("/apis/board-article")
@RequiredArgsConstructor
@Slf4j
public class BoardArticleController {

    private final BoardArticleService boardArticleService;

    @GetMapping("{boardGroupUid}")
    public List<BoardArticle> boardArticleList(@PathVariable(value = "boardGroupUid") Long boardGroupUid) {
        List<BoardArticle> boardArticleList = boardArticleService.boardArticleList(boardGroupUid);
        return boardArticleList;
    }

    @GetMapping("/detail/{uid}")
    public BoardArticle detailBoardArticle(@PathVariable(value = "uid") Long uid) {
        BoardArticle detailBoardArticle = boardArticleService.detailBoardArticle(uid);
        return detailBoardArticle;
    }

//    @PostMapping
//    public String saveBoardArticle(@RequestBody BoardArticle boardArticle) {
//        int saveBoardArticle = boardArticleService.insertBoardArticle(boardArticle);
//        if (saveBoardArticle == 1) {
//            return "success";
//        } else {
//            return "fail";
//        }
//    }
}
