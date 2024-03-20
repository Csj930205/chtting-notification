package com.example.ssetest.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 * @author sjChoi
 * @since 3/19/24
 */
@Getter
@Entity
@Table(name = "unread_notification")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UnreadNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "parent_uid")
    private Long parentUid;

    @Column(name = "message")
    private String message;

    @Column(name = "member_uid")
    private String memberUid;

    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private Date createdDate;


}
