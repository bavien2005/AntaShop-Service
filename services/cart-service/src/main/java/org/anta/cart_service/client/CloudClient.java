package org.anta.cart_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CloudClient {
    private final RestTemplate restTemplate;

    @Value("${cloud.service.url}")
    private String cloudServiceUrl;

    public FileMetadataDTO getMainImage(Long productId) {
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
    }
}