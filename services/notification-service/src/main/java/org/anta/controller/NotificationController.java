package org.anta.controller;

import org.anta.entity.NotificationRequestEntity;
import org.anta.repository.NotificationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.anta.util.JsonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.anta.dto.request.NotificationEmailRequest;
import org.anta.dto.response.NotificationResponse;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRequestRepository repo;
    private final RabbitTemplate rabbitTemplate;

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

}
