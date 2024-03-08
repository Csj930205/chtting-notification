package com.example.ssetest.repository;

import com.example.ssetest.domain.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sjChoi
 * @since 2/14/24
 */
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {

    List<ChattingRoom> findAll();

    ChattingRoom findChattingRoomByUid(Long uid);
}
