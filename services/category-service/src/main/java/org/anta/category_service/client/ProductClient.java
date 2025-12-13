package org.anta.category_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public int deleteProductsByCategory(Long categoryId) {
        String url = productServiceUrl + "/by-category/" + categoryId;

        ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = resp.getBody();
        if (body == null) return 0;

        Object deleted = body.get("deletedProducts");
        if (deleted instanceof Number n) return n.intValue();

        return 0;
    }
}

