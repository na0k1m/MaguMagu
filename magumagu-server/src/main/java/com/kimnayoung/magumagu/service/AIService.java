package com.kimnayoung.magumagu.service;

import com.kimnayoung.magumagu.dto.AIRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AIService {
    private final RestClient restClient;
    private final String apiKey;

    // 생성자를 통해 RestClient와 application.yml에 API 키를 주입받음
    public AIService(@Value("${ai.nvidia.api-key}") String apiKey) {
        this.restClient = RestClient.builder().baseUrl("https://integrate.api.nvidia.com/v1").build();
        this.apiKey = apiKey;
    }

    public String testVisionApi() {
        // 1. DTO 조립: 텍스트 내용 세팅
        AIRequestDto.Content textContent = AIRequestDto.Content.builder()
                .type("text")
                .text("이 사진에 무엇이 있는지 간단히 한글로 설명해 줘.")
                .build();

        // 2. DTO 조립: 이미지 URL 세팅
        AIRequestDto.Content imageContent = AIRequestDto.Content.builder()
                .type("image_url")
                .image_url(AIRequestDto.ImageUrl.builder()
                        .url("https://picsum.photos/id/237/400/300")
                        .build())
                .build();

        // 3. 메시지 묶기
        AIRequestDto.Message message = AIRequestDto.Message.builder()
                .role("user")
                .content(List.of(textContent, imageContent))
                .build();

        // 4. 최종 요청 객체 완성
        AIRequestDto.Request requestBody = AIRequestDto.Request.builder()
                .model("nvidia/nemotron-3-nano-omni-30b-a3b-reasoning")
                .messages(List.of(message))
                .max_tokens(2048)
                .build();

        // 5. NVIDIA API로 발송 및 응답 반환
        return restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }
}
