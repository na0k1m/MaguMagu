package com.kimnayoung.magumagu.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 태그 이름 중복 허용 x
    private String name;

    @Builder
    public Tag(String name) {
        this.name = name;
    }
}