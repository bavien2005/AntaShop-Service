package org.anta.controller;

import lombok.extern.slf4j.Slf4j;
import org.anta.dto.request.ProductRequest;
import org.anta.dto.response.ProductResponse;
import org.anta.entity.Product;
import org.anta.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categorySlug
    ) {
        return ResponseEntity.ok(productService.getAllFiltered(title, categorySlug));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest){
        ProductResponse productResponse = productService.addProduct(productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse>  update(@PathVariable Long id , @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id ){
        productService.deleteProduct(id);
        return ResponseEntity.ok("Deleted id: " +id +" Successfully");
    }


    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductResponse>> getAllProductByName(@PathVariable String name){
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping("/sync-images/{id}")
    public ResponseEntity<ProductResponse> syncImages(@PathVariable Long id) {
        ProductResponse resp = productService.syncImagesFromCloud(id);
        return ResponseEntity.ok(resp);
    }

    /**
     * Gán sản phẩm vào danh mục
     */
    @PutMapping("/{productId}/category/{categoryId}")
    public ResponseEntity<Product> assignCategory(
            @PathVariable Long productId,
            @PathVariable Long categoryId) {

        Product updated = productService.assignCategory(productId, categoryId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa sản phẩm khỏi danh mục
     */
    @DeleteMapping("/{productId}/category")
    public ResponseEntity<Product> removeCategory(@PathVariable Long productId) {
        Product updated = productService.removeCategory(productId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam(name = "q", required = false) String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> byCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(productService.listByCategory(categoryId));
    }

    @DeleteMapping("/by-category/{categoryId}")
    public ResponseEntity<Map<String, Object>> deleteByCategory(@PathVariable Long categoryId) {
        int deleted = productService.deleteProductsByCategory(categoryId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "categoryId", categoryId,
                "deletedProducts", deleted
        ));

    }

}
