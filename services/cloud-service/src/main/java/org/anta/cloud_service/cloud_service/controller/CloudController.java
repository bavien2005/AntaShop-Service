package org.anta.cloud_service.cloud_service.controller;


import lombok.RequiredArgsConstructor;
import org.anta.cloud_service.cloud_service.entity.FileMetadata;
import org.anta.cloud_service.cloud_service.service.CloudService;
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


    @GetMapping("/product/productId:/{productId}")
    public ResponseEntity<Map<List<FileMetadata> , String> >getFilesByProductId(
            @PathVariable Long productId) {

        return ResponseEntity.ok(Map.of(cloudService.getByProductId(productId) ,
                "File retrieval Successful"));
    }


    @PostMapping("/upload-multiple")
    public ResponseEntity<List<FileMetadata>>uploadFile(
              @RequestParam("files") List<MultipartFile> files ,
              @RequestParam("uploaderId") Long uploaderId) {
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
}
