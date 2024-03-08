package com.example.ssetest.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author sjChoi
 * @since 2/29/24
 */
@Getter
@Table(name = "chatting_room_participants")
@Builder(toBuilder = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChattingRoomParticipants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "chatting_room_uid")
    private Long chattingRoomUid;

    @Column(name = "participants_uid")
    private String participantsUid;

    @Column(name = "joined_date", updatable = false)
    private Date joinedDate;
}
