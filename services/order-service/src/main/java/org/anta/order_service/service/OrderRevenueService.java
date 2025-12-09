package org.anta.order_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.order_service.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderRevenueService {
    private final OrderItemRepository orderItemsRepository;

    public Double getActualRevenue() {
        Double revenue = orderItemsRepository.sumRevenueFromCompletedOrders();
        return revenue != null ? revenue : 0.0;
    }
}