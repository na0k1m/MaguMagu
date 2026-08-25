package com.kimnayoung.magumagu.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Category {

    @Id // Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 1, 2, 3... DB가 알아서 번호를 매겨줌
    private Long id;

    @Column(nullable = false, unique = true) // 이름은 필수이고, 중복될 수 없도록 통제
    private String name;

    @Builder
    public Category(String name) {
        this.name = name;
    }
}