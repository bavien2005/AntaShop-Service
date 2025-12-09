package org.anta.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CategoryClient {

    private final RestTemplate restTemplate;

    @Value("${category.service.url}")
    private String categoryServiceUrl;

    /**
     * Gọi Category-Service để lấy Category theo ID
     * @return CategoryResponse hoặc throw RuntimeException nếu không tồn tại
     */
    public CategoryResponse getCategoryById(Long categoryId) {
        try {
            String url = categoryServiceUrl + "/api/categories/" + categoryId;
            return restTemplate.getForObject(url, CategoryResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to Category-Service", e);
        }
    }

    /**
     * Kiểm tra Category có tồn tại không (true/false)
     */
    public boolean existsCategory(Long categoryId) {
        try {
            String url = categoryServiceUrl + "/api/categories/" + categoryId;
            restTemplate.getForObject(url, CategoryResponse.class);
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to Category-Service", e);
        }
    }
}