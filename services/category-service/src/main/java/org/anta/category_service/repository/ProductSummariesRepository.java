package org.anta.category_service.repository;

import org.anta.category_service.entity.ProductSummaries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSummariesRepository extends JpaRepository<ProductSummaries, Long> {
    List<ProductSummaries> findByCategoryId(Long CategoryId);
    boolean existsBySlug(String slug);
}
