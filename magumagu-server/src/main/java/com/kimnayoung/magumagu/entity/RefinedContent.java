package com.kimnayoung.magumagu.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class RefinedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 긴 텍스트를 담기 위해 columnDefinition = "TEXT"를 사용
    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalContent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String refinedText;

    // 생성 일시는 DB가 알아서 찍어주도록 설정
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 여러 개의 기록(N)은 하나의 카테고리(1)에 속한다
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public RefinedContent(String originalContent, String refinedText, Category category) {
        this.originalContent = originalContent;
        this.refinedText = refinedText;
        this.category = category;
    }

    @OneToMany(mappedBy = "refinedContent")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("refinedContent") // 무한루프 방지
    private java.util.List<ContentTagMap> tags = new java.util.ArrayList<>();
}