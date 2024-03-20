package com.example.ssetest.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author sjChoi
 * @since 3/18/24
 */
@Getter
@Builder(toBuilder = true)
@Document(indexName = "gw-log-*")
@NoArgsConstructor
@AllArgsConstructor
public class Error {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String message;

}
