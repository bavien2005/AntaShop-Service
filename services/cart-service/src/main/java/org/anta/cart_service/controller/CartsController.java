package org.anta.cart_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.request.CartItemsRequest;
import org.anta.cart_service.dto.response.CartsResponse;
import org.anta.cart_service.entity.Carts;
import org.anta.cart_service.mapper.CartsMapper;
import org.anta.cart_service.service.CartsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartsController {

    private final CartsService cartsService;
    private final CartsMapper cartsMapper;

    /**
        Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/add")
    public ResponseEntity<CartsResponse> addItemToCart(@RequestBody CartItemsRequest request) {
        Carts cart = cartsService.AddItemsToCarts(request);
        return ResponseEntity.ok(cartsMapper.toResponse(cart));
    }

    /**
     *  [GET] Lấy giỏ hàng hiện tại theo userId hoặc sessionId
     * Ví dụ: /api/cart/current?userId=1
     * Hoặc:  /api/cart/current?sessionId=abc123
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentCart(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String sessionId
    ) {
        Optional<Carts> optionalCart = cartsService.getCurrentCart(userId, sessionId);

        if (optionalCart.isEmpty()) {
            // 204 nếu chưa có giỏ hàng
            return ResponseEntity.noContent().build();
        }

        // Convert sang response DTO
        CartsResponse response = cartsMapper.toResponse(optionalCart.get());
        return ResponseEntity.ok(response);
    }


    /**
     * [DELETE] Xoá 1 sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId) {
        cartsService.DeleteItemsOutCart(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [DELETE] Xoá toàn bộ sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartsService.DeleteFullItemsOutCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{cartId}/items/quantity")
    public ResponseEntity<?> updateItemQuantity(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam(required = false) Long variantId,
            @RequestParam Long newQuantity) {

        try {
            Carts updatedCart = cartsService.updateItemQuantity(cartId, productId, variantId, newQuantity);

            if (updatedCart != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Cập nhật số lượng thành công");
                response.put("cart", updatedCart);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy sản phẩm trong giỏ hàng");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật số lượng: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @PostMapping("/merge")
    public ResponseEntity<?> mergeCart(
            @RequestParam String sessionId,
            @RequestParam Long userId
    ) {
        Carts merged = cartsService.mergeCart(sessionId, userId);
        if (merged == null) {
            return ResponseEntity.ok(msg("Không có giỏ để merge"));
        }
        return ResponseEntity.ok(msg("Merge thành công", cartsMapper.toResponse(merged)));
    }
    // ============================================================
    // Helper chuẩn JSON response
    // ============================================================
    private Object msg(String message) {
        return new Object() {
            public final boolean success = true;
            public final String msg = message;
        };
    }

    private Object msg(String message, Object data) {
        return new Object() {
            public final boolean success = true;
            public final String msg = message;
            public final Object payload = data;
        };
    }
}

