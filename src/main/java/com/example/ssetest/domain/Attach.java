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
 * @since 3/22/24
 */
@Getter
@Entity
@Table(name = "attach")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Attach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "table_type")
    private String tableType;

    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "saved_name")
    private String savedName;

    @Column(name = "saved_dir")
    private String savedDir;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private String size;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date", updatable = false)
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "del_yn", columnDefinition = "varchar(1) default 'N' ")
    private String delYn;

}
