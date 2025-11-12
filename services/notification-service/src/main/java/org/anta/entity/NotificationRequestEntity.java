
package org.anta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestEntity {

    @Id
    private String id;

    private String type;

    private String channel;

    @Lob
    private String payload;

    private String status;

    private Integer attempts;

    private String idempotencyKey;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
