package org.anta.repository;



import org.anta.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    @Query(value = """
        WITH RECURSIVE months AS (
            SELECT 1 AS month
            UNION ALL
            SELECT month + 1 FROM months WHERE month < 12
        )
        SELECT 
            :year AS year,
            m.month,
            COALESCE(COUNT(u.id), 0) AS count
        FROM months m
        LEFT JOIN users u 
               ON MONTH(u.created_at) = m.month 
              AND YEAR(u.created_at) = :year
        GROUP BY m.month
        ORDER BY m.month
        """, nativeQuery = true)
    List<Object[]> countUsersByMonthFull(@Param("year") int year);

}
