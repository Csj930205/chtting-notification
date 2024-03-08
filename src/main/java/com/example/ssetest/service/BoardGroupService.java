package com.example.ssetest.service;

import com.example.ssetest.domain.BoardGroup;
import com.example.ssetest.repository.BoardGroupRepository;
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
public class BoardGroupService {

    private final BoardGroupRepository boardGroupRepository;

    @Transactional(readOnly = true)
    public List<BoardGroup> findAllBoardGroupList() {
        List<BoardGroup> findAll = boardGroupRepository.findAll();
        return findAll;
    }

    @Transactional(readOnly = true)
    public BoardGroup detailBoardGroup(Long uid) {
        BoardGroup boardGroup = boardGroupRepository.findByUid(uid);
        if (boardGroup != null) {
            return boardGroup;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public BoardGroup findByBoardGroup(String name) {
        BoardGroup boardGroup = boardGroupRepository.findByName(name);

        if (boardGroup != null) {
            return boardGroup;
        } else {
            return null;
        }
    }

    @Transactional
    public int saveBoardGroup(BoardGroup boardGroup) {
        BoardGroup findByBoardGroup = findByBoardGroup(boardGroup.getName());
        if (findByBoardGroup == null) {
            boardGroupRepository.save(boardGroup);
            return 1;
        } else {
            return 0;
        }
    }
}
