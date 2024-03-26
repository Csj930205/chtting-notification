package com.example.ssetest.controller;

import com.example.ssetest.dto.ChattingAttachRequestDto;
import com.example.ssetest.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sjChoi
 * @since 3/26/24
 */
@RestController
@RequestMapping("/apis/attaches")
@RequiredArgsConstructor
public class AttachController {

    private final AttachService attachService;

    @GetMapping("/view/{uid}")
    public void viewAttach(@PathVariable(value = "uid") String uid) {
        attachService.viewAttach(uid);
    }

    @PostMapping("upload")
    public Map<String, Object> uploadAttach(@RequestPart(value = "uploadFile", required = false) MultipartFile multipartFile,
                                            @RequestPart(value = "chattingRoom") ChattingAttachRequestDto chattingAttachRequestDto) {
        String insert = attachService.insertAttach(multipartFile, chattingAttachRequestDto.getUid(), chattingAttachRequestDto.getTableType());
        Map<String, Object> result = new HashMap<>();
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());
        result.put("url", insert);
        return result;
    }
}
