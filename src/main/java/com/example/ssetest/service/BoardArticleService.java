package com.example.ssetest.service;

import com.example.ssetest.domain.BoardArticle;
import com.example.ssetest.repository.BoardArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/20/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardArticleService {

    private final BoardArticleRepository boardArticleRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<BoardArticle> boardArticleList(Long uid) {
        List<BoardArticle> boardArticleList = boardArticleRepository.findByBoardGroupUid(uid);

        return boardArticleList;
    }

    @Transactional(readOnly = true)
    public BoardArticle detailBoardArticle(Long uid) {
        BoardArticle detailBoardArticle = boardArticleRepository.findByUid(uid);
        return detailBoardArticle;
    }

    @Transactional
    public int insertBoardArticle(BoardArticle boardArticle) {
        boardArticleRepository.save(boardArticle);
        notificationService.notificationBoard(boardArticle);
        return 1;
    }
}
