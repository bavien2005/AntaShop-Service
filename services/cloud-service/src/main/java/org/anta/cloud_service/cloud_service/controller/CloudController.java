package org.anta.cloud_service.cloud_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anta.cloud_service.cloud_service.entity.FileMetadata;
import org.anta.cloud_service.cloud_service.service.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cloud")
@RequiredArgsConstructor
public class CloudController {

    private final CloudService cloudService;


    private final Logger log = LoggerFactory.getLogger(CloudController.class);

    @PostMapping("/upload-debug")
    public ResponseEntity<String> uploadDebug(HttpServletRequest req) {
        String ct = req.getContentType();
        log.info("DEBUG Content-Type = {}", ct);
        try {
            if (req.getParts() != null) {
                log.info("Parts count: {}", req.getParts().size());
                req.getParts().forEach(p -> log.info("Part name='{}' size={} contentType={}", p.getName(), p.getSize(), p.getContentType()));
            }
        } catch (Exception ex) {
            log.warn("Cannot read parts: {}", ex.toString());
        }
        return ResponseEntity.ok("checked");
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<FileMetadata>> getFilesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(cloudService.getByProductId(productId));
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileMetadata>> uploadFile(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "uploaderId", required = false) Long uploaderId) {

        if (files == null || files.isEmpty()) {
            log.info("No files received, uploaderId={}", uploaderId);
            throw new IllegalArgumentException("No files provided");
        }
        if (uploaderId == null) uploaderId = 0L; // fallback if you want
        return ResponseEntity.ok(cloudService.uploadMultiple(files, uploaderId));
    }
    @PutMapping("/update-product/{productId}")
    public ResponseEntity<Map<String, String>> updateProductImages(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIds) {
        cloudService.assignImagesToProduct(productId, imageIds);
        return ResponseEntity.ok(Map.of("message", "Images linked successfully"));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanup() {
        cloudService.cleanUnusedFiles();
        return ResponseEntity.ok(Map.of("message", "Old temp files deleted"));
    }

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable Long fileId) {
        cloudService.deleteFileById(fileId); // implement ở service
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }
}