package com.example.ssetest.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

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
@DynamicInsert
@DynamicUpdate
public class ChattingRoomParticipants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "chatting_room_uid")
    private Long chattingRoomUid;

    @Column(name = "participants_uid")
    private String participantsUid;

    @Column(name = "joined_date", updatable = false)
    @CreationTimestamp
    private Date joinedDate;

    @Column(name = "leave_date", insertable = false, updatable = false)
    private Date leaveDate;

    @Column(name = "connect_yn", columnDefinition = "varchar(1) default 'N' ")
    private String connectYn;

    @Column(name = "unread_message", columnDefinition = "int default '0' ")
    private int unreadMessage;
}
