package com.kimnayoung.magumagu.service;

import com.kimnayoung.magumagu.dto.AiRequesetDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AiService {
    private final RestClient restClient;
    private final String apiKey;

    // 생성자를 통해 RestClient와 application.yml에 API 키를 주입받음
    public AiService(@Value("${ai.nvidia.api-key}") String apiKey) {
        this.restClient = RestClient.builder().baseUrl("https://integrate.api.nvidia.com/v1").build();
        this.apiKey = apiKey;
    }

    public String testVisionApi() {
        String systemPrompt = """
                너는 사용자의 무질서한 텍스트 메모, 링크, 이미지를 완벽하게 분석하고 체계적으로 분류하는 서비스의 핵심 AI 비서야.
                사용자가 데이터를 입력하면, 반드시 아래의 규칙을 준수하여 오직 JSON 형식으로만 응답해야 해. JSON 외에 어떠한 부가적인 설명이나 인사말도 절대 출력하지 마.
                
                [분류 규칙]
                1. category: 입력된 내용의 목적과 맥락을 파악하여 반드시 다음 6가지 대분류 중 하나만 선택해.
                   ["일정", "정보", "아이디어", "쇼핑", "추억", "기타"]
                2. format: 입력된 데이터의 형태를 파악하여 다음 3가지 중 하나를 선택해.
                   ["text", "image", "link"]
                3. tags: 나중에 사용자가 쉽게 검색할 수 있도록 핵심 키워드를 3~5개의 배열(Array) 형태로 추출해.
                4. summary: 입력된 내용의 핵심을 파악하여 20자 이내의 직관적인 한 줄 제목을 작성해.
                5. extracted_text: 이미지에 텍스트가 포함되어 있다면 빠짐없이 추출해서 적고, 단순 텍스트 메모라면 맞춤법을 교정하여 저장하고, 링크라면 URL 주소를 그대로 적어. (해당하는 내용이 없다면 빈 문자열 ""을 반환해.)
                
                [응답 형식 (JSON)]
                {
                  "category": "선택된 카테고리",
                  "format": "선택된 데이터 형태",
                  "tags": ["태그1", "태그2", "태그3"],
                  "summary": "한 줄 요약 제목",
                  "extracted_text": "추출되거나 정리된 텍스트/링크 내용"
                }
                """;

        AiRequesetDto.Content systemContent = AiRequesetDto.Content.builder()
                .type("text")
                .text(systemPrompt)
                .build();

        AiRequesetDto.Message systemMessage = AiRequesetDto.Message.builder()
                .role("system")
                .content(List.of(systemContent))
                .build();
        
        // 1. DTO 조립: 텍스트 내용 세팅
        AiRequesetDto.Content textContent = AiRequesetDto.Content.builder()
                .type("text")
                .text("이 사진에 무엇이 있는지 간단히 한글로 설명해 줘.")
                .build();

        // 2. DTO 조립: 이미지 URL 세팅
        AiRequesetDto.Content imageContent = AiRequesetDto.Content.builder()
                .type("image_url")
                .image_url(AiRequesetDto.ImageUrl.builder()
                        .url("https://picsum.photos/id/237/400/300")
                        .build())
                .build();

        // 3. 메시지 묶기
        AiRequesetDto.Message userMessage = AiRequesetDto.Message.builder()
                .role("user")
                .content(List.of(textContent, imageContent))
                .build();

        // 4. 최종 요청 객체 완성
        AiRequesetDto.Request requestBody = AiRequesetDto.Request.builder()
                .model("nvidia/nemotron-3-nano-omni-30b-a3b-reasoning")
                .messages(List.of(systemMessage, userMessage))
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
