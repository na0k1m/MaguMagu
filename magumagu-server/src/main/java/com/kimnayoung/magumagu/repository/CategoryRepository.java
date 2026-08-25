package com.kimnayoung.magumagu.repository;

import com.kimnayoung.magumagu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<관리할_엔티티_이름, 그_엔티티의_PK_타입>
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name); 
}