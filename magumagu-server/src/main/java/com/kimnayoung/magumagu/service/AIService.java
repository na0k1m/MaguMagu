package com.kimnayoung.magumagu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimnayoung.magumagu.dto.AiParsedResultDto;
import com.kimnayoung.magumagu.dto.AiRequestDto;
import com.kimnayoung.magumagu.dto.AiResponseDto;
import com.kimnayoung.magumagu.entity.*;
import com.kimnayoung.magumagu.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AiService {
    private final RestClient restClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final RefinedContentRepository refinedContentRepository;
    private final ContentTagMapRepository contentTagMapRepository;

    // 생성자를 통해 RestClient와 application.yml에 API 키를 주입받음
    public AiService(@Value("${ai.nvidia.api-key}") String apiKey,
                     CategoryRepository categoryRepository,
                     TagRepository tagRepository,
                     RefinedContentRepository refinedContentRepository,
                     ContentTagMapRepository contentTagMapRepository) {
        this.restClient = RestClient.builder().baseUrl("https://integrate.api.nvidia.com/v1").build();
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.refinedContentRepository = refinedContentRepository;
        this.contentTagMapRepository = contentTagMapRepository;
    }

    public AiParsedResultDto analyzeData(String userText, String imageUrl) {
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
                5. extracted_text: 입력된 데이터(이미지/텍스트)의 내용을 단순히 옮겨 적지 마. 
                반드시 핵심 내용만 파악하여, 불필요한 서술어는 모두 제거하고 [개조식 요약] 형태로 완벽하게 정제해서 작성해.
                
                [응답 형식 (JSON)]
                {
                  "category": "선택된 카테고리",
                  "format": "선택된 데이터 형태",
                  "tags": ["태그1", "태그2", "태그3"],
                  "summary": "한 줄 요약 제목",
                  "extracted_text": "추출되거나 정리된 텍스트/링크 내용"
                }
                """;

        AiRequestDto.Content systemContent = AiRequestDto.Content.builder()
                .type("text")
                .text(systemPrompt)
                .build();

        AiRequestDto.Message systemMessage = AiRequestDto.Message.builder()
                .role("system")
                .content(List.of(systemContent))
                .build();

        java.util.List<AiRequestDto.Content> userContents = new java.util.ArrayList<>();

        // 텍스트 추가
        if (userText != null && !userText.trim().isEmpty()) {
            userContents.add(AiRequestDto.Content.builder()
                    .type("text")
                    .text(userText)
                    .build());
        }
        
        // 이미지 URL 추가
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            userContents.add(AiRequestDto.Content.builder()
                    .type("image_url")
                    .image_url(AiRequestDto.ImageUrl.builder().url(imageUrl).build())
                    .build());
        }

        // 메시지 묶기
        AiRequestDto.Message userMessage = AiRequestDto.Message.builder()
                .role("user")
                .content(userContents)
                .build();

        // 최종 요청 객체 완성
        AiRequestDto.Request requestBody = AiRequestDto.Request.builder()
                .model("nvidia/nemotron-3-nano-omni-30b-a3b-reasoning")
                .messages(List.of(systemMessage, userMessage))
                .max_tokens(2048)
                .build();

        // API 발송 및 AiResponseDto로 1차 파싱
        AiResponseDto response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .body(requestBody)
                .retrieve()
                .body(AiResponseDto.class);

        // content 텍스트 추출
        String content = response.getChoices().get(0).getMessage().getContent();

        if (content.startsWith("```json")) {
            content = content.substring(7, content.length() - 3).trim();
        } else if (content.startsWith("```")) {
            content = content.substring(3, content.length() - 3).trim();
        }

        try {
            return objectMapper.readValue(content, AiParsedResultDto.class);
        } catch (Exception e) {
            throw new RuntimeException("AI 응답 파싱 실패: " + content, e);
        }
    }

    // AI 분석 결과를 4개의 DB 테이블에 정돈해서 저장하는 메서드
    @Transactional
    public RefinedContent saveRefinedData(String originalText, String parsedCategory, String parsedRefinedText, List<String> parsedTags) {
        
        // 1. 카테고리 처리
        Category category = categoryRepository.findByName(parsedCategory)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(parsedCategory).build()));

        // 2. 메인 기록 저장
        RefinedContent content = RefinedContent.builder()
                .originalContent(originalText)
                .refinedText(parsedRefinedText)
                .category(category)
                .build();
        refinedContentRepository.save(content);

        // 3. 태그 맵핑 처리
        // 만약 태그가 null이라면 빈 리스트로 처리해서 에러를 방지
        if (parsedTags != null) {
            for (String tagName : parsedTags) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

                ContentTagMap tagMap = ContentTagMap.builder()
                        .refinedContent(content)
                        .tag(tag)
                        .build();
                contentTagMapRepository.save(tagMap);
                content.getTags().add(tagMap);
            }
        }

        return content;
    }
}
