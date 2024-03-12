package com.example.ssetest.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sjChoi
 * @since 3/11/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private String key;

    private String value;

    private Long expiredTime;
}
