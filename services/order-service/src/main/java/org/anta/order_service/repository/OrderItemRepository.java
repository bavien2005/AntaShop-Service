package org.anta.order_service.repository;

import org.anta.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
//    @Query(value = "SELECT SUM(oi.quantity * oi.unit_price) " +
//            "FROM order_items oi JOIN orders o ON oi.order_id = o.id " +
//            "WHERE o.status IN ('PAID','COMPLETED')", nativeQuery = true)
//    Double sumRevenueFromCompletedOrders();

    @Query(value = """
    SELECT 
      CONCAT(
        YEAR(o.created_at),
        '-W',
        LPAD(WEEK(o.created_at, 1), 2, '0')
      ) AS week,
      SUM(oi.quantity * oi.unit_price) AS revenue
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE o.status IN ('PAID','DELIVERED')
    GROUP BY 
      CONCAT(
        YEAR(o.created_at),
        '-W',
        LPAD(WEEK(o.created_at, 1), 2, '0')
      )
    ORDER BY 
      CONCAT(
        YEAR(o.created_at),
        '-W',
        LPAD(WEEK(o.created_at, 1), 2, '0')
      )
    """,
            nativeQuery = true)
    List<Object[]> sumWeeklyRevenueFromCompletedOrders();

    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);

    @Query(value = """
    SELECT 
      oi.product_id AS productId,
      COALESCE(SUM(oi.quantity), 0) AS soldQty
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.id
    WHERE o.status IN ('PAID','DELIVERED')
      AND oi.product_id IS NOT NULL
    GROUP BY oi.product_id
    ORDER BY soldQty DESC
""", nativeQuery = true)
    List<Object[]> sumSoldQtyByProductFromPaidOrDelivered();

}