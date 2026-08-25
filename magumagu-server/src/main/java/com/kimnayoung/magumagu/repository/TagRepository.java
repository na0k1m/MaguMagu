package com.kimnayoung.magumagu.repository;

import com.kimnayoung.magumagu.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    // 태그 이름으로 검색하는 기능
    Optional<Tag> findByName(String name);
}