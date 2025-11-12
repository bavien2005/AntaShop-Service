package org.anta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anta.dto.request.ProductVariantRequest;
import org.anta.dto.request.PurchaseItemRequest;
import org.anta.dto.response.ProductVariantResponse;
import org.anta.service.ProductVariantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productVariant")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @GetMapping("/listVariant/productId/{productId}")
    public ResponseEntity<List<ProductVariantResponse>> getListVariantByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productVariantService.findByProduct(productId));
    }

    @GetMapping("/variant/{id}")
    public ResponseEntity<ProductVariantResponse> getVariant(@PathVariable Long id) {
        return ResponseEntity.ok(productVariantService.getById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ProductVariantResponse> add(@RequestBody ProductVariantRequest req) {
        return ResponseEntity.ok(productVariantService.add(req));
    }

    @PutMapping("/update/variantId/{id}")
    public ResponseEntity<ProductVariantResponse> update(@PathVariable Long id, @RequestBody ProductVariantRequest req) {
        return ResponseEntity.ok(productVariantService.update(id, req));
    }

    @DeleteMapping("/delete/variantId/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productVariantService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve/id/{id}")
    public ResponseEntity<?>  reserve(@PathVariable("id") Long id ,
                                       @Valid @RequestBody PurchaseItemRequest purchaseItemRequest){
        productVariantService.reserveStock(id , purchaseItemRequest.getQuantity());
        return ResponseEntity.ok().body("reserve");
    }

    @PostMapping("/release/id/{id}")
    public ResponseEntity<?> release(@PathVariable("id") Long id ,
                                     @Valid @RequestBody PurchaseItemRequest purchaseItemRequest){
        productVariantService.releaseStock(id,  purchaseItemRequest.getQuantity());
        return ResponseEntity.ok().body("released");
    }
}
