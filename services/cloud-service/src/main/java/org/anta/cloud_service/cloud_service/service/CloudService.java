package org.anta.cloud_service.cloud_service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.anta.cloud_service.cloud_service.entity.FileMetadata;
import org.anta.cloud_service.cloud_service.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.*;

@Service
@RequiredArgsConstructor
public class CloudService {

    private final FileMetadataRepository fileMetadataRepository;
    private final Cloudinary cloudinary;

    public List<FileMetadata> uploadMultiple(List<MultipartFile> files , Long uploaderId)  {

        List<FileMetadata> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");
                String format = (String) uploadResult.get("format");
                String resourceType = (String) uploadResult.get("resource_type");

                FileMetadata metadata = FileMetadata.builder()
                        .publicId(publicId)
                        .url(url)
                        .productId(null)
                        .uploaderId(uploaderId)
                        .uploadedAt(LocalDateTime.now())
                        .build();

                metadata = fileMetadataRepository.save(metadata);

                results.add(metadata);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }

        return results;
    }

    @Transactional
    public void assignImagesToProduct(Long productId, List<Long> imageIds) {
        fileMetadataRepository.updateProductIds(productId, imageIds);
    }

    public List<FileMetadata> getByProductId(Long productId) {
        return fileMetadataRepository.findByProductId(productId);
    }

    @Transactional
    public void cleanUnusedFiles() {
        fileMetadataRepository.deleteOldTempFiles();
    }

}
