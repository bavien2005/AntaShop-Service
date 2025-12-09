package org.anta.services.revenue_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.services.revenue_service.dto.RevenueDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.cart.url}")
    private String cartServiceUrl;

    @Value("${services.order.url}")
    private String orderServiceUrl;

    public RevenueDTO getRevenueComparison() {
        Double expected = webClientBuilder.build()
                .get()
                .uri(cartServiceUrl + "/cart/revenue/expected")
                .retrieve()
                .bodyToMono(Double.class)
                .block();

        Double actual = webClientBuilder.build()
                .get()
                .uri(orderServiceUrl + "/orders/revenue/actual")
                .retrieve()
                .bodyToMono(Double.class)
                .block();

        return new RevenueDTO(expected != null ? expected : 0.0,
                actual != null ? actual : 0.0);
    }
}