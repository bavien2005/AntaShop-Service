package org.anta.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryClient {

    private final RestTemplate restTemplate;

    @Value("${category.service.url}") // ví dụ: http://localhost:8087/api/categories
    private String categoryServiceUrl;

    /** GET {base}/{id} */
    public CategoryResponse getCategoryById(Long categoryId) {
        try {
            String url = categoryServiceUrl + "/" + categoryId;
            return restTemplate.getForObject(url, CategoryResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to Category-Service", e);
        }
    }

    /** HEAD/GET {base}/{id} để kiểm tra tồn tại */
    public boolean existsCategory(Long categoryId) {
        try {
            String url = categoryServiceUrl + "/" + categoryId;
            restTemplate.getForObject(url, CategoryResponse.class);
            return true;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to Category-Service", e);
        }
    }

    /** GET {base}/grouped -> Map<String, List<CategoryResponse>> */
    public Map<String, List<CategoryResponse>> getGrouped() {
        try {
            String url = categoryServiceUrl + "/grouped";
            ResponseEntity<Map<String, List<CategoryResponse>>> resp =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<Map<String, List<CategoryResponse>>>() {}
                    );
            return Optional.ofNullable(resp.getBody()).orElseGet(Collections::emptyMap);
        } catch (Exception e) {
            // Tùy bạn: có thể ném lỗi để BE trả 5xx, hoặc trả rỗng để FE vẫn sống
            // Ở đây trả rỗng và log tuỳ bạn
            return Collections.emptyMap();
        }
    }

    /** Tìm categoryId theo title + slug, dùng dữ liệu từ /grouped */
    public Optional<Long> resolveCategoryId(String title, String slug) {
        if (title == null || slug == null) return Optional.empty();

        String titleKey = title.toLowerCase();
        String slugKey  = slug.toLowerCase();

        Map<String, List<CategoryResponse>> grouped = getGrouped();

        // Một số service trả key "men"/"MEN"… -> normalize về lowerCase
        for (Map.Entry<String, List<CategoryResponse>> e : grouped.entrySet()) {
            String k = e.getKey() == null ? "" : e.getKey().toLowerCase();
            if (!k.equals(titleKey)) continue;

            for (CategoryResponse c : e.getValue()) {
                String s = c.getSlug() == null ? "" : c.getSlug().toLowerCase();
                if (s.equals(slugKey)) {
                    return Optional.ofNullable(c.getId());
                }
            }
        }
        return Optional.empty();
    }
}
