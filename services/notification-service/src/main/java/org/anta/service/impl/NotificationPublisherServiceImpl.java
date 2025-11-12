package org.anta.service.impl;


import lombok.RequiredArgsConstructor;
import org.anta.dto.request.NotificationEmailRequest;
import org.anta.entity.NotificationRequestEntity;
import org.anta.repository.NotificationRequestRepository;
import org.anta.service.NotificationPublisherService;
import org.anta.util.JsonUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPublisherServiceImpl implements NotificationPublisherService {

    private final NotificationRequestRepository repo;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public String publishEmail(NotificationEmailRequest req) {
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
        return requestId;
    }
}
