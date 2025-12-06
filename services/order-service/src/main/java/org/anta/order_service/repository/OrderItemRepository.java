package org.anta.order_service.repository;

import org.anta.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;



public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}