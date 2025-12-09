package org.anta.cart_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ProductClient {
    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public ProductDTO getProductById(Long productId) {
        return restTemplate.getForObject(
                productServiceUrl + "/" + productId,
                ProductDTO.class
        );
    }
}