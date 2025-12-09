// org/anta/services/revenue_service/service/DashboardService.java
package org.anta.services.revenue_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.services.revenue_service.dto.WeeklyRevenueComparisonDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.cart.url}")
    private String cartServiceUrl;

    @Value("${services.order.url}")
    private String orderServiceUrl;

    // 🔥 Chỉ dùng method này để FE vẽ biểu đồ
    public List<WeeklyRevenueComparisonDTO> getWeeklyRevenueComparison() {
        WebClient client = webClientBuilder.build();

        // ----- Gọi cart-service: /cart/revenue/weekly -----
        List<Map<String, Object>> expectedList = client.get()
                .uri(cartServiceUrl + "/api/cart/revenue/weekly")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .blockOptional()
                .orElse(Collections.emptyList());

        // ----- Gọi order-service: /orders/revenue/weekly -----
        List<Map<String, Object>> actualList = client.get()
                .uri(orderServiceUrl + "/api/orders/revenue/weekly")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .blockOptional()
                .orElse(Collections.emptyList());

        // ----- Merge theo key "week" -----
        Map<String, WeeklyRevenueComparisonDTO> map = new HashMap<>();

        if (expectedList != null) {
            for (Map<String, Object> row : expectedList) {
                String week = Objects.toString(row.get("week"), "");
                Double revenue = row.get("revenue") != null
                        ? ((Number) row.get("revenue")).doubleValue()
                        : 0.0;

                map.computeIfAbsent(
                        week,
                        w -> new WeeklyRevenueComparisonDTO(w, 0.0, 0.0)
                ).setExpectedRevenue(revenue);
            }
        }

        if (actualList != null) {
            for (Map<String, Object> row : actualList) {
                String week = Objects.toString(row.get("week"), "");
                Double revenue = row.get("revenue") != null
                        ? ((Number) row.get("revenue")).doubleValue()
                        : 0.0;

                map.computeIfAbsent(
                        week,
                        w -> new WeeklyRevenueComparisonDTO(w, 0.0, 0.0)
                ).setActualRevenue(revenue);
            }
        }

        // Sắp xếp theo week (format "YYYY-Www" thì sort string là đi được)
        return map.values().stream()
                .sorted(Comparator.comparing(WeeklyRevenueComparisonDTO::getWeek))
                .collect(Collectors.toList());
    }
}
