package com.example.ssetest.repository;

import com.example.ssetest.domain.BoardGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/19/24
 */
public interface BoardGroupRepository extends JpaRepository<BoardGroup, Long> {

    List<BoardGroup> findAll();

    BoardGroup findByName(String name);

    BoardGroup findByUid(Long uid);


}
