package org.anta.controller;

import jakarta.validation.Valid;
import org.anta.dto.request.OrderSuccessEmailRequest;
import org.anta.entity.NotificationRequestEntity;
import org.anta.repository.NotificationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.anta.service.NotificationPublisherService;
import org.anta.service.impl.NotificationPublisherServiceImpl;
import org.anta.util.JsonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.anta.dto.request.NotificationEmailRequest;
import org.anta.dto.response.NotificationResponse;
import org.springframework.validation.annotation.Validated;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRequestRepository repo;
    private final RabbitTemplate rabbitTemplate;
    private final NotificationPublisherService publisherService;
    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(@RequestBody @Validated NotificationEmailRequest req) {
        String requestId = UUID.randomUUID().toString();

        NotificationRequestEntity entity = NotificationRequestEntity.builder()
                .id(requestId)
                .type("EMAIL")
                .channel("EMAIL")
                .payload(JsonUtil.toJson(req))
                .status("PENDING")
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .idempotencyKey(req.getIdempotencyKey())
                .build();

        repo.save(entity);

        rabbitTemplate.convertAndSend("notifications-exchange",
                "notifications.email", requestId);

        return ResponseEntity.accepted().body(new NotificationResponse(true,
                "Accepted", requestId));
    }

    @PostMapping("/order-success")
    public ResponseEntity<NotificationResponse> sendOrderSuccess(@RequestBody @Valid OrderSuccessEmailRequest req) {

        // build template data
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", req.getCustomerName() == null ? "bạn" : req.getCustomerName());
        data.put("orderNumber", req.getOrderNumber());

        if (req.getTotal() != null) {
            // format VND kiểu 1.234.567
            String vnd = NumberFormat.getInstance(new Locale("vi", "VN")).format(req.getTotal());
            data.put("total", vnd);
        }

        // build NotificationEmailRequest
        NotificationEmailRequest mail = new NotificationEmailRequest();
        mail.setTo(req.getTo());
        mail.setSubject("Xác nhận đơn hàng #" + req.getOrderNumber() + " - ANTA Việt Nam");
        mail.setTemplateId("order_success_v1");
        mail.setTemplateData(data);

        // idempotency: ổn định theo orderNumber + email
        String key = (req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank())
                ? req.getIdempotencyKey()
                : ("order_success:" + req.getOrderNumber() + ":" + req.getTo());
        mail.setIdempotencyKey(key);

        String requestId = publisherService.publishEmail(mail);
        return ResponseEntity.accepted().body(new NotificationResponse(true, "Accepted", requestId));
    }
}
