package org.anta.cart_service.repository;

import org.anta.cart_service.dto.response.TopProductDTO;
import org.anta.cart_service.entity.CartItems;
import org.anta.cart_service.entity.Carts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    Optional<CartItems> findByCartIdAndProductIdAndVariantId(Long cartId,
                          Long productId, Long variantId);

    @Modifying
    @Transactional
    void deleteByCartId(Long cartId);

    //dashboard
    @Query(value = "SELECT ci.product_id AS productId, ci.product_name AS productName, SUM(ci.quantity) AS totalQuantity " +
            "FROM cart_items ci GROUP BY ci.product_id, ci.product_name " +
            "ORDER BY SUM(ci.quantity) DESC LIMIT 10", nativeQuery = true)
    List<Object[]> findTop10ProductsNative();

//    @Query(value = "SELECT SUM(ci.quantity * ci.unit_price) " +
//            "FROM cart_items ci JOIN carts c ON ci.cart_id = c.id " +
//            "WHERE c.status = 'OPEN'", nativeQuery = true)
//    Double sumRevenueFromOpenCarts();

    @Query(value = """
        SELECT 
            DATE_FORMAT(ci.created_at, '%x-W%v') AS week_label,
            SUM(ci.quantity * ci.unit_price) AS total
        FROM cart_items ci
        JOIN carts c ON ci.cart_id = c.id
        WHERE c.status = 'OPEN'
          AND ci.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 WEEK)
        GROUP BY week_label
        ORDER BY week_label
    """, nativeQuery = true)
    List<Object[]> sumRevenueFromOpenCartsByWeek();

}
