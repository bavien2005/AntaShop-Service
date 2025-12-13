package org.anta.repository;

import org.anta.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    //Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> searchByNameIgnoreAccent(@Param("name") String name);

    // ✅ NEW: search q in name/brand/description (accent-insensitive via collation)
    @Query(value = """
        SELECT * FROM products p
        WHERE (
            p.name        COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', :q, '%')
            OR p.brand    COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', :q, '%')
            OR p.description COLLATE utf8mb4_0900_ai_ci LIKE CONCAT('%', :q, '%')
        )
        ORDER BY p.id DESC
        """, nativeQuery = true)
    List<Product> searchFullTextLoose(@Param("q") String q);
}
