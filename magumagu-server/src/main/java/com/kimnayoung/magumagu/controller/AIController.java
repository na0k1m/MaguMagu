package com.kimnayoung.magumagu.controller;

import com.kimnayoung.magumagu.dto.AiParsedResultDto;
import com.kimnayoung.magumagu.entity.RefinedContent;
import com.kimnayoung.magumagu.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/magu")
public class AiController {
    private final AiService aiService;

    // 생성자를 통해 AiService 주입
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createMagu(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // 1. 입력 데이터 정리
            // 만약 텍스트를 안 보냈다면 에러가 나지 않도록 빈 문자열로 처리
            String originalText = (text != null) ? text : "";
            
            String imageUrl = "";
            if (image != null && !image.isEmpty()) {
                // 이미지를 Base64 문자열로 인코딩합니다.
                String base64Image = java.util.Base64.getEncoder().encodeToString(image.getBytes());
                // 파일의 타입(예: image/png)을 가져와서 AI가 인식할 수 있는 포맷으로 조립합니다.
                String mimeType = image.getContentType();
                imageUrl = "data:" + mimeType + ";base64," + base64Image;
            }

            // 2. AI에게 데이터 분석 요청 (첫 번째 무기 발사!)
            AiParsedResultDto parsedResult = aiService.analyzeData(originalText, imageUrl);

            // 3. 분석된 결과를 DB에 체계적으로 저장 (두 번째 무기 발사!)
            RefinedContent savedContent = aiService.saveRefinedData(
                    originalText, 
                    parsedResult.getCategory(), 
                    parsedResult.getExtracted_text(), // AI가 추출해 준 텍스트/결과물
                    parsedResult.getTags()
            );

            // 4. 저장 완료된 결과를 화면(Swagger나 앱)에 보여줍니다.
            return ResponseEntity.ok(savedContent);

        } catch (Exception e) {
            // 에러가 나면 무슨 에러인지 친절하게 알려줍니다.
            return ResponseEntity.internalServerError().body("저장 중 에러 발생: " + e.getMessage());
        }
    }
}
