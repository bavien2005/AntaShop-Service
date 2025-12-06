// service/OrderService.java
package org.anta.order_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anta.order_service.client.PaymentClient;
import org.anta.order_service.client.ProductReservationClient;
import org.anta.order_service.dto.external.*;
import org.anta.order_service.dto.request.CreateOrderRequest;
import org.anta.order_service.dto.request.OrderItemRequest;
import org.anta.order_service.dto.response.CreateOrderResponse;
import org.anta.order_service.dto.response.OrderResponse;
import org.anta.order_service.entity.Order;
import org.anta.order_service.entity.OrderItem;
import org.anta.order_service.enums.OrderStatus;
import org.anta.order_service.mapper.OrderMapper;
import org.anta.order_service.repository.OrderItemRepository;
import org.anta.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final ProductReservationClient reservationClient;
    private final PaymentClient paymentClient;
    private final OrderMapper mapper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items required");
        }

        // 1) Tính tiền từ FE gửi (ở step thực tế nên validate với price server-side)
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest it : req.getItems()) {
            // ở đây giả sử đơn giá lấy từ FE hoặc tra thêm ở product-service (bạn có thể thêm Feign để lấy giá)
            // tạm thời: đơn giá = 0 -> để flow, thực tế nên bắt buộc FE truyền unitPrice hoặc ta query variant price
            BigDecimal unit = BigDecimal.ZERO;
            BigDecimal line = unit.multiply(BigDecimal.valueOf(it.getQuantity() != null ? it.getQuantity() : 0));
            total = total.add(line);
        }

        // 2) Tạo bản ghi Order (trạng thái PENDING_PAYMENT)
        Order order = Order.builder()
                .userId(req.getUserId())
                .status(OrderStatus.PENDING_PAYMENT)
                .totalAmount(total)
                .build();
        order = orderRepo.save(order);

        // 3) OrderItems
        for (OrderItemRequest it : req.getItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .productId(it.getProductId())
                    .variantId(it.getVariantId())
                    .quantity(it.getQuantity())
                    .unitPrice(BigDecimal.ZERO)  // TODO: set đơn giá thật
                    .lineTotal(BigDecimal.ZERO)  // TODO: unit*qty
                    .build();
            itemRepo.save(oi);
            order.getItems().add(oi);
        }

        // 4) Gọi product-service để tạo reservation giữ tồn
        CreateReservationRequest r = CreateReservationRequest.builder()
                .userId(req.getUserId())
                .ttlSeconds(900) // 15 phút
                .items(req.getItems().stream()
                        .map(i -> ReservationItem.builder()
                                .variantId(i.getVariantId())
                                .quantity(i.getQuantity())
                                .build())
                        .toList())
                .build();

        CreateReservationResponse rResp = reservationClient.create(r);
        Long reservationId = rResp.getReservationId();
        order.setReservationId(reservationId);
        orderRepo.save(order);

        // 5) Gọi payment-service để tạo thanh toán (MoMo)
        long amountVnd = order.getTotalAmount() == null ? 0L : order.getTotalAmount().longValue();
        CreatePaymentResponse pay = paymentClient.create(CreatePaymentRequest.builder()
                .orderId(order.getId())
                .userId(req.getUserId())
                .amount(amountVnd)
                .build());

        order.setPayUrl(pay.getPayUrl());
        orderRepo.save(order);

        return CreateOrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .payUrl(order.getPayUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id){
        Order o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return mapper.toResponse(o);
    }

    /**
     * Được payment-service gọi khi IPN đã xác thực:
     *  - status = SUCCESS => confirm reservation, set order=PAID
     *  - status = FAILED  => cancel reservation, set order=FAILED
     */
    @Transactional
    public void updatePaymentStatus(Long orderId, String paymentStatus) {
        Order o = orderRepo.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
            // xác nhận reservation để trừ tồn
            if (o.getReservationId() != null) {
                reservationClient.confirm(o.getReservationId());
            }
            o.setStatus(OrderStatus.PAID);
        } else {
            // huỷ reservation, trả hàng về kho
            if (o.getReservationId() != null) {
                reservationClient.cancel(o.getReservationId());
            }
            o.setStatus("FAILED".equalsIgnoreCase(paymentStatus) ? OrderStatus.FAILED : OrderStatus.CANCELLED);
        }
        orderRepo.save(o);
    }

    @Transactional
    public void cancelOrder(Long id){
        Order o = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (o.getStatus() == OrderStatus.PAID) return; // đã thanh toán thì không huỷ
        if (o.getReservationId() != null) {
            reservationClient.cancel(o.getReservationId());
        }
        o.setStatus(OrderStatus.CANCELLED);
        orderRepo.save(o);
    }
}
