package org.anta.cloud_service.cloud_service.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.anta.cloud_service.cloud_service.entity.FileMetadata;
import org.anta.cloud_service.cloud_service.service.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestBody(required = false) Object body) {

        // body có thể là List<Long> (legacy) hoặc Map { ids: [...], mainId: ... }
        List<Long> imageIds = null;
        Long mainId = null;

        if (body == null) {
            // clear existing files for product
            cloudService.assignImagesToProduct(productId, List.of(), null);
            return ResponseEntity.ok(Map.of("message", "Images unassigned"));
        }

        if (body instanceof List) {
            // legacy: List of ids
            @SuppressWarnings("unchecked")
            List<Object> raw = (List<Object>) body;
            imageIds = raw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
        } else if (body instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) body;
            Object idsObj = map.get("ids");
            if (idsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> raw = (List<Object>) idsObj;
                imageIds = raw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
            } else if (idsObj == null && map.get("imageIds") instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> raw = (List<Object>) map.get("imageIds");
                imageIds = raw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
            }
            // mainId may be present
            if (map.get("mainId") != null) {
                mainId = Long.valueOf(String.valueOf(map.get("mainId")));
            } else if (map.get("main") != null) {
                mainId = Long.valueOf(String.valueOf(map.get("main")));
            }
        } else {
            // attempt to coerce via Jackson -> Map
            try {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> map = (java.util.Map<String, Object>) body;
                Object idsObj = map.get("ids");
                if (idsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> raw = (List<Object>) idsObj;
                    imageIds = raw.stream().map(o -> Long.valueOf(String.valueOf(o))).collect(Collectors.toList());
                }
                if (map.get("mainId") != null) mainId = Long.valueOf(String.valueOf(map.get("mainId")));
            } catch (Exception ex) {
                log.warn("Cannot parse body for update-product: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid payload"));
            }
        }

        cloudService.assignImagesToProduct(productId, imageIds == null ? List.of() : imageIds, mainId);
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