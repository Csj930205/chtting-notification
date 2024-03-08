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
@Table(name = "board_group")
@NoArgsConstructor
public class BoardGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    private String name;

    private String mandatory;
}
