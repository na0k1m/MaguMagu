package com.kimnayoung.magumagu.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class MaguItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB 자동 생성 번호 (Primary Key)

    private String category; // 일정, 정보, 아이디어 등
    private String format;   // text, image, link

    @ElementCollection // List 타입을 별도의 테이블로 관리
    private List<String> tags;

    private String summary;

    @Column(columnDefinition = "TEXT") // 텍스트가 길어질 수 있으므로 크기 늘림
    private String extractedText;

    private String originalText; // 원본 데이터도 저장
    private String originalImageUrl;

    @Builder
    public MaguItem(String category, String format, List<String> tags, String summary, 
                    String extractedText, String originalText, String originalImageUrl) {
        this.category = category;
        this.format = format;
        this.tags = tags;
        this.summary = summary;
        this.extractedText = extractedText;
        this.originalText = originalText;
        this.originalImageUrl = originalImageUrl;
    }
}
