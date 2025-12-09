package org.anta.cart_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.repository.CartItemsRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartRevenueService {
    private final CartItemsRepository cartItemsRepository;

    public Double getExpectedRevenue() {
        Double revenue = cartItemsRepository.sumRevenueFromOpenCarts();
        return revenue != null ? revenue : 0.0;
    }
}