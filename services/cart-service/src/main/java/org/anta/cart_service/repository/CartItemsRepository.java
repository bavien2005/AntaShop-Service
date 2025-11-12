package org.anta.cart_service.repository;

import org.anta.cart_service.entity.CartItems;
import org.anta.cart_service.entity.Carts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    Optional<CartItems> findByCartIdAndProductIdAndVariantId(Long cartId,
                          Long productId, Long variantId);
    void deleteByCartId(Long cartId);

}
