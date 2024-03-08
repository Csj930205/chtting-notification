package com.example.ssetest.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author sjChoi
 * @since 2/19/24
 */
@Getter
@Entity
@Table(name = "board_group_user")
@NoArgsConstructor
public class BoardGroupUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "board_group_uid")
    private Long boardGroupUid;

    @Column(name = "board_group_username")
    private String boardGroupUsername;
}
