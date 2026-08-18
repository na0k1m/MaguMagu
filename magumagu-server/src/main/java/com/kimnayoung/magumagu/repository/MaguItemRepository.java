package com.kimnayoung.magumagu.repository;

import com.kimnayoung.magumagu.entity.MaguItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaguItemRepository extends JpaRepository<MaguItem, Long> {
}