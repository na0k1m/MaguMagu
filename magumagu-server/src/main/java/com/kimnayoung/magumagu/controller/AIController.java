package com.kimnayoung.magumagu.controller;

import com.kimnayoung.magumagu.dto.AiParsedResultDto;
import com.kimnayoung.magumagu.dto.MaguRequestDto;
import com.kimnayoung.magumagu.entity.MaguItem;
import com.kimnayoung.magumagu.repository.MaguItemRepository;
import com.kimnayoung.magumagu.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/magu")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;
    private final MaguItemRepository maguItemRepository;

    @PostMapping
    public ResponseEntity<MaguItem> createMagu(@RequestBody MaguRequestDto requestDto) {
        
        // 1. AI 서비스 호출하여 분석 요청
        AiParsedResultDto aiResult = aiService.analyzeData(requestDto.getText(), requestDto.getImageUrl());

        // 2. 분석 결과를 바탕으로 DB에 저장할 Entity 조립
        MaguItem newItem = MaguItem.builder()
                .category(aiResult.getCategory())
                .format(aiResult.getFormat())
                .tags(aiResult.getTags())
                .summary(aiResult.getSummary())
                .extractedText(aiResult.getExtracted_text())
                .originalText(requestDto.getText())
                .originalImageUrl(requestDto.getImageUrl())
                .build();

        // 3. DB에 저장
        MaguItem savedItem = maguItemRepository.save(newItem);

        // 4. 저장된 결과를 클라이언트에게 반환
        return ResponseEntity.ok(savedItem);
    }
}
