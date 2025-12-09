package org.anta.order_service.repository;

import org.anta.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query(value = "SELECT SUM(oi.quantity * oi.unit_price) " +
            "FROM order_items oi JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status IN ('PAID','COMPLETED')", nativeQuery = true)
    Double sumRevenueFromCompletedOrders();

}