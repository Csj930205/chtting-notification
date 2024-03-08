package com.example.ssetest.service;

import com.example.ssetest.domain.BoardGroupUser;
import com.example.ssetest.repository.BoardGroupUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardGroupUserService {

    private final BoardGroupUserRepository boardGroupUserRepository;

    @Transactional(readOnly = true)
    public List<BoardGroupUser> findByBoardGroupUid(Long boardGroupUid) {
        List<BoardGroupUser> findByBoardGroupUid = boardGroupUserRepository.findByBoardGroupUid(boardGroupUid);
        return findByBoardGroupUid;
    }

    @Transactional(readOnly = true)
    public List<BoardGroupUser> findByUsername(String boardGroupUsername) {
        List<BoardGroupUser> findByUsername = boardGroupUserRepository.findByBoardGroupUsername(boardGroupUsername);

        return findByUsername;
    }

    @Transactional
    public int saveBoardGroupUser(BoardGroupUser boardGroupUser) {
        BoardGroupUser detailBoardGroupUser = boardGroupUserRepository.findByBoardGroupUidAndBoardGroupUsername(boardGroupUser.getBoardGroupUid(), boardGroupUser.getBoardGroupUsername());
        if (detailBoardGroupUser == null) {
            boardGroupUserRepository.save(boardGroupUser);
            return 1;
        } else {
            return 0;
        }
    }
}
