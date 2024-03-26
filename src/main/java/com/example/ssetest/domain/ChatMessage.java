package com.example.ssetest.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
 * @author sjChoi
 * @since 2/15/24
 */
@Getter
@Builder(toBuilder = true)
@Document(indexName = "gw-chat-topic*")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String roomUid;

    @Field(type = FieldType.Text)
    private String senderUid;

    @Field(type = FieldType.Text)
    private String content;

    @Field(name = "@timestamp", type = FieldType.Date)
    private Date timestamp;
    
}
