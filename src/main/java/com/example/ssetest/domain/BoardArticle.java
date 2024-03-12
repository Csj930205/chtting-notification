package com.example.ssetest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * @author sjChoi
 * @since 2/20/24
 */
@Getter
@Entity
@Table(name = "board_article")
@NoArgsConstructor
public class BoardArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "board_group_uid")
    private Long boardGroupUid;

    private String content;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private Date createdDate;
}
