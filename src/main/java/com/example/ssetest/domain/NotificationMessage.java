package com.example.ssetest.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 알림 메세지
 * @author sjChoi
 * @since 2/7/24
 */
@Getter
@Builder(toBuilder = true)
@Document(indexName = "gw-notification-topic*")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field(type = FieldType.Long)
    private Long parentUid;

    @Field(type = FieldType.Text)
    private String memberUid;

    @Field(type = FieldType.Text)
    private String type;

    @Field(type = FieldType.Text)
    private String message;
}
