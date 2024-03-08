package com.example.ssetest.repository;

import com.example.ssetest.domain.BoardGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
public interface BoardGroupUserRepository extends JpaRepository<BoardGroupUser, Long> {

    List<BoardGroupUser> findByBoardGroupUid(Long boardGroupUid);

    List<BoardGroupUser> findByBoardGroupUsername(String boardGroupUsername);

    BoardGroupUser findByBoardGroupUidAndBoardGroupUsername(Long boardGroupUid, String boardGroupUsername);
}
