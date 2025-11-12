package org.anta.cart_service.controller;

import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.request.CartItemsRequest;
import org.anta.cart_service.dto.response.CartsResponse;
import org.anta.cart_service.entity.Carts;
import org.anta.cart_service.mapper.CartsMapper;
import org.anta.cart_service.service.CartsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartsController {

    private final CartsService cartsService;
    private final CartsMapper cartsMapper;

    /**
     * 🛒 [POST] Thêm sản phẩm vào giỏ hàng
     * Body: AddCartItemRequest
     */
    @PostMapping("/add")
    public ResponseEntity<CartsResponse> addItemToCart(@RequestBody CartItemsRequest request) {
        Carts cart = cartsService.AddItemsToCarts(request);
        return ResponseEntity.ok(cartsMapper.toResponse(cart));
    }

    /**
     * 🔍 [GET] Lấy giỏ hàng hiện tại theo userId hoặc sessionId
     * Ví dụ: /api/cart/current?userId=1
     * Hoặc:  /api/cart/current?sessionId=abc123
     */
    @GetMapping("/current")
    public ResponseEntity<CartsResponse> getCurrentCart(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String sessionId
    ) {
        Optional<Carts> optionalCart = cartsService.getCurrentCart(userId, sessionId);

        if (optionalCart.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 nếu chưa có cart
        }

        CartsResponse response = cartsMapper.toResponse(optionalCart.get());
        return ResponseEntity.ok(response);
    }

    /**
     * ❌ [DELETE] Xoá 1 sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long itemId) {
        cartsService.DeleteItemsOutCart(itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🧹 [DELETE] Xoá toàn bộ giỏ hàng
     */
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartsService.DeleteFullItemsOutCart(cartId);
        return ResponseEntity.noContent().build();
    }
}

