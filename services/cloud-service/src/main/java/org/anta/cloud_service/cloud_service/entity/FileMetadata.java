package org.anta.cloud_service.cloud_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.naming.Name;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "file_metadata")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "format")
    private String format;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name="IsMain" )
    private Boolean isMain;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

}
