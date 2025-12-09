// org/anta/order_service/service/OrderRevenueService.java
package org.anta.order_service.service;

import lombok.RequiredArgsConstructor;
import org.anta.order_service.dto.response.WeeklyRevenueDTO;
import org.anta.order_service.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderRevenueService {

    private final OrderItemRepository orderItemsRepository;

    // DOANH THU THỰC TẾ THEO TUẦN
    public List<WeeklyRevenueDTO> getActualRevenueWeekly() {
        List<Object[]> rows = orderItemsRepository.sumWeeklyRevenueFromCompletedOrders();
        List<WeeklyRevenueDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            String weekLabel = (String) row[0];
            Double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            result.add(new WeeklyRevenueDTO(weekLabel, total));
        }

        return result;
    }
}
