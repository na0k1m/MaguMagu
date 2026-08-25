package com.kimnayoung.magumagu.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ContentTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 매핑 테이블의 입장에서는 기록(RefinedContent)도 '1'이고, 자기 자신이 'N'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refined_content_id")
    private RefinedContent refinedContent;

    // 매핑 테이블의 입장에서는 태그(Tag)도 '1'이고, 자기 자신이 'N'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public ContentTagMap(RefinedContent refinedContent, Tag tag) {
        this.refinedContent = refinedContent;
        this.tag = tag;
    }
}