package org.anta.order_service.repository;

import org.anta.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    JOIN orders o ON oi.order_id = o.order_id
    WHERE o.status IN ('PAID','COMPLETED')
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


}