package org.anta.cart_service.repository;

import org.anta.cart_service.entity.CartItems;
import org.anta.cart_service.entity.Carts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    Optional<CartItems> findByCartIdAndProductIdAndVariantId(Long cartId,
                          Long productId, Long variantId);

    @Modifying
    @Transactional
    void deleteByCartId(Long cartId);

}
