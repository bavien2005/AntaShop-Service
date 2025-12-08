package org.anta.cloud_service.cloud_service.repository;


import org.anta.cloud_service.cloud_service.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata , Long> {


    @Modifying
    @Query("UPDATE FileMetadata f SET f.productId = :productId WHERE f.id IN :ids")
    void updateProductIds(@Param("productId") Long productId, @Param("ids") List<Long> ids);

    @Query("SELECT f FROM FileMetadata f WHERE f.productId = :productId ORDER BY f.isMain DESC, f.id ASC")
    List<FileMetadata> findByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM FileMetadata f WHERE f.productId IS NULL AND f.uploadedAt < :cutoff")
    void deleteTempFilesOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("UPDATE FileMetadata f SET f.isMain = false WHERE f.productId = :productId")
    void resetIsMainForProduct(@Param("productId") Long productId);

    @Modifying
    @Query("UPDATE FileMetadata f SET f.productId = null, f.isMain = false WHERE f.productId = :productId")
    void clearProductForProductId(@Param("productId") Long productId);
    List<FileMetadata> findByProductIdIsNullAndUploadedAtBefore(LocalDateTime cutoff);
}

