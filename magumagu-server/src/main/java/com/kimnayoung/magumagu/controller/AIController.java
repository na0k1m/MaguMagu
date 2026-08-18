package com.kimnayoung.magumagu.controller;

import com.kimnayoung.magumagu.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    /**
     * AI Vision API 연동 테스트 엔드포인트
     * GET http://localhost:8080/api/ai/test
     */
    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> testVision() {
        String response = aiService.testVisionApi();
        return ResponseEntity.ok(response);
    }
}
