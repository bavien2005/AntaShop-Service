package org.anta.cloud_service.cloud_service.repository;


import org.anta.cloud_service.cloud_service.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata , Long> {

    List<FileMetadata> findByProductId(Long productId);

    @Modifying
    @Query("UPDATE FileMetadata f SET f.productId = :productId WHERE f.id IN :ids")
    void updateProductIds(@Param("productId") Long productId, @Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "DELETE FROM file_metadata WHERE uploaded_at < (NOW() - INTERVAL 7 DAY)", nativeQuery = true)
    void deleteOldTempFiles();
}
