// org/anta/cart_service/service/CartRevenueService.java
package org.anta.cart_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.response.WeeklyRevenueDTO;
import org.anta.cart_service.repository.CartItemsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartRevenueService {

    private final CartItemsRepository cartItemsRepository;

    // DOANH THU DỰ KIẾN THEO TUẦN
    public List<WeeklyRevenueDTO> getExpectedRevenueWeekly() {
        List<Object[]> rows = cartItemsRepository.sumRevenueFromOpenCartsByWeek();
        List<WeeklyRevenueDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            String weekLabel = (String) row[0]; // "2025-W01"
            Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            result.add(new WeeklyRevenueDTO(weekLabel, total));
        }

        return result;
    }
}
