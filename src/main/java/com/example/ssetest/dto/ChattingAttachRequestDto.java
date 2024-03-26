package com.example.ssetest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author sjChoi
 * @since 3/26/24
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChattingAttachRequestDto {

    private Long uid;

    private String tableType;
}
