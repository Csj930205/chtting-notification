package com.example.ssetest.controller;

import com.example.ssetest.domain.Error;
import com.example.ssetest.service.ErrorElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sjChoi
 * @since 3/18/24
 */
@RestController
@RequestMapping("/apis/error")
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorElasticsearchService errorElasticsearchService;

    @GetMapping("error-list")
    public Map<String, Object> getErrorList(@RequestParam(required = false) Map<String, Object> paramMap) {
        Map<String, Object> result = new HashMap<>();
        List<Error> errorList = errorElasticsearchService.errorList(paramMap);
        result.put("result", "success");
        result.put("code", HttpStatus.OK.value());
        result.put("errorList", errorList);
        return result;
    }
}
