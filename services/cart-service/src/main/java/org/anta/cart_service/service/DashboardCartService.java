package org.anta.cart_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.response.TopProductDTO;
import org.anta.cart_service.repository.CartItemsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardCartService {
    private final CartItemsRepository cartItemsRepository;

    public List<TopProductDTO> getTop10Products() {
        List<Object[]> results = cartItemsRepository.findTop10ProductsNative();
        return results.stream()
                .map(r -> new TopProductDTO(
                        ((Number) r[0]).longValue(),   // productId
                        (String) r[1],                 // productName
                        ((Number) r[2]).longValue()    // totalQuantity
                ))
                .collect(Collectors.toList());
    }
}