package org.anta.repository;


import org.anta.entity.NotificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRequestRepository
        extends JpaRepository<NotificationRequestEntity, String> {

    Optional<NotificationRequestEntity> findByIdempotencyKey(String idempotencyKey);
}