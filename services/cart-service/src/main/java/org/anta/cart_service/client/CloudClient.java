// file: src/main/java/org/anta/cart_service/client/CloudClient.java
package org.anta.cart_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudClient {
    private final RestTemplate restTemplate;

    @Value("${cloud.service.url}")
    private String cloudServiceUrl;

    public FileMetadataDTO getMainImage(Long productId) {
        try {
            FileMetadataDTO[] files = restTemplate.getForObject(
                    cloudServiceUrl + "/product/" + productId,
                    FileMetadataDTO[].class
            );

            if (files == null || files.length == 0) {
                return null;
            }

            return Arrays.stream(files)
                    .filter(FileMetadataDTO::isMain)
                    .findFirst()
                    .orElse(files[0]);
        } catch (Exception e) {
            // Không để exception làm fail toàn bộ flow — trả null để BE tiếp tục lưu item (không có ảnh)
            log.warn("[CloudClient] getMainImage failed for product {} -> {}", productId, e.toString());
            return null;
        }
    }
}
