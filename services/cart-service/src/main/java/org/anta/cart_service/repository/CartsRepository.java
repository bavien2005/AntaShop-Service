package org.anta.cart_service.repository;

import org.anta.cart_service.entity.Carts;
import org.anta.cart_service.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartsRepository extends JpaRepository<Carts, Long> {
    Optional<Carts> findByUserIdAndStatus(Long userId, Status status);
    Optional<Carts> findBySessionIdAndStatus(String sessionId, Status status);
}
