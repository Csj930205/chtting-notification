package com.example.ssetest.controller;

import com.example.ssetest.service.UnreadNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sjChoi
 * @since 3/19/24
 */
@RestController
@RequestMapping("/apis/unread")
@RequiredArgsConstructor
public class UnreadNotificationController {

    private final UnreadNotificationService unreadNotificationService;

    @GetMapping("/notification/{memberUid}")
    public Map<String, Object> unreadNotificationCount(@PathVariable(value = "memberUid") String memberUid) {
        Map<String, Object> result = new HashMap<>();
        int count = unreadNotificationService.countUnreadNotification(memberUid);
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());
        result.put("count", count);
        return result;
    }

    @DeleteMapping("{memberUid}")
    public Map<String, Object> deleteUnreadNotification(@PathVariable(value = "memberUid") String memberUid) {
        Map<String, Object> result = new HashMap<>();
        unreadNotificationService.deleteUnreadNotification(memberUid);
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());
        return result;
    }
}
