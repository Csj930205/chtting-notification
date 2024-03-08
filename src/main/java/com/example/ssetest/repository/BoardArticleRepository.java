package com.example.ssetest.repository;

import com.example.ssetest.domain.BoardArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/20/24
 */
public interface BoardArticleRepository extends JpaRepository<BoardArticle, Long> {
    List<BoardArticle> findByBoardGroupUid(Long uid);

    BoardArticle findByUid(Long uid);

}
