package com.kimnayoung.magumagu.controller;

import com.kimnayoung.magumagu.dto.AiParsedResultDto;
import com.kimnayoung.magumagu.dto.MaguRequestDto;
import com.kimnayoung.magumagu.entity.MaguItem;
import com.kimnayoung.magumagu.repository.MaguItemRepository;
import com.kimnayoung.magumagu.service.AiService;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/magu")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;
    private final MaguItemRepository maguItemRepository;

    @PostMapping
    public ResponseEntity<MaguItem> createMagu(
            @RequestParam(value = "text", required = false) String text, // 텍스트 메모
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        
        String imageUrlForAi = "";
        String originalFileName = "";

        try {
            // 1. 사진 파일이 들어왔다면? AI가 읽을 수 있는 Base64 텍스트로 변환!
            if (imageFile != null && !imageFile.isEmpty()) {
                originalFileName = imageFile.getOriginalFilename();
                String contentType = imageFile.getContentType(); 
                byte[] imageBytes = imageFile.getBytes(); 
                
                String base64Data = Base64.getEncoder().encodeToString(imageBytes);
                imageUrlForAi = "data:" + contentType + ";base64," + base64Data;
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지 변환 중 오류가 발생했습니다.", e);
        }

        // 1. AI 서비스 호출하여 분석 요청
        AiParsedResultDto aiResult = aiService.analyzeData(text, imageUrlForAi);

        // 2. 분석 결과를 바탕으로 DB에 저장할 Entity 조립
        MaguItem newItem = MaguItem.builder()
                .category(aiResult.getCategory())
                .format(aiResult.getFormat())
                .tags(aiResult.getTags())
                .summary(aiResult.getSummary())
                .extractedText(aiResult.getExtracted_text())
                .originalText(text)
                .originalImageUrl(originalFileName)
                .build();

        // 3. DB에 저장
        MaguItem savedItem = maguItemRepository.save(newItem);

        // 4. 저장된 결과를 클라이언트에게 반환
        return ResponseEntity.ok(savedItem);
    }
}
