package org.anta.cart_service.service;


import lombok.RequiredArgsConstructor;
import org.anta.cart_service.client.CloudClient;
import org.anta.cart_service.client.FileMetadataDTO;
import org.anta.cart_service.client.ProductClient;
import org.anta.cart_service.client.ProductDTO;
import org.anta.cart_service.dto.request.CartItemsRequest;
import org.anta.cart_service.entity.CartItems;
import org.anta.cart_service.entity.Carts;
import org.anta.cart_service.enums.Status;
import org.anta.cart_service.repository.CartItemsRepository;
import org.anta.cart_service.repository.CartsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartsService {
    private final CartsRepository cartsRepository;
    private final CartItemsRepository cartItemsRepository;
    private final ProductClient productClient;
    private final CloudClient cloudClient;

    private Carts createNewCart(CartItemsRequest req) {
        Carts newCart = new Carts();
        newCart.setUserId(req.getUserId());
        newCart.setSessionId(req.getSessionId());
        newCart.setStatus(Status.OPEN);
        LocalDateTime now = LocalDateTime.now();
        newCart.setCreatedAt(now);
        newCart.setUpdatedAt(now);
        return cartsRepository.save(newCart);
    }
    //them sp vao gio
    // thêm sp vào giỏ
    @Transactional
    public Carts AddItemsToCarts(CartItemsRequest req) {
        Optional<Carts> optionalCarts;
        if (req.getUserId() != null) {
            optionalCarts = cartsRepository.findByUserIdAndStatus(req.getUserId(), Status.OPEN);
        } else {
            optionalCarts = cartsRepository.findBySessionIdAndStatus(req.getSessionId(), Status.OPEN);
        }

        Carts cart = optionalCarts.orElseGet(() -> createNewCart(req));

        Optional<CartItems> cartItems = cartItemsRepository.findByCartIdAndProductIdAndVariantId(
                cart.getId(), req.getProductId(), req.getVariantId()
        );

        CartItems items;

        if (cartItems.isPresent()) {
            // Nếu sản phẩm đã tồn tại thì cộng thêm số lượng
            items = cartItems.get();
            items.setQuantity(
                    (items.getQuantity() == null ? 0 : items.getQuantity()) + req.getQuantity()
            );
            items.setUpdatedAt(LocalDateTime.now());
        } else {
            // Nếu chưa có thì tạo mới
            // Gọi sang product-service để lấy thông tin sản phẩm
            ProductDTO product = productClient.getProductById(req.getProductId());

            // Gọi sang cloud-service để lấy ảnh chính của sản phẩm
            FileMetadataDTO file = cloudClient.getMainImage(req.getProductId());

            items = new CartItems();
            items.setCart(cart);
            items.setProductId(req.getProductId());
            items.setVariantId(req.getVariantId());
            items.setProductName(product.getName());                 // lấy từ product-service
            items.setUnitPrice(product.getPrice().doubleValue());    // lấy từ product-service
            items.setImageUrl(file != null ? file.getUrl() : null);  // lấy từ cloud-service
            items.setQuantity(req.getQuantity());
            items.setCreatedAt(LocalDateTime.now());
            items.setUpdatedAt(LocalDateTime.now());
        }

        cartItemsRepository.save(items);

        // Cập nhật thời gian giỏ hàng
        cart.setUpdatedAt(LocalDateTime.now());
        cartsRepository.save(cart);

        return cart;
    }

    //xoa 1

    public void DeleteItemsOutCart(Long idItems){
        cartItemsRepository.deleteById(idItems);
    }

    //xoa full

    public void DeleteFullItemsOutCart(Long cartId){
        cartItemsRepository.deleteByCartId(cartId);
    }

    /**
     * Xem giỏ hàng hiện tại (theo user hoặc session)
     */

    public Optional<Carts> getCurrentCart(Long userId, String sessionId) {
        if (userId != null) {
            Optional<Carts> userCart = cartsRepository.findByUserIdAndStatus(userId, Status.OPEN);
            if (userCart.isPresent()) {
                return userCart;
            }
        }

        if (sessionId != null) {
            return cartsRepository.findBySessionIdAndStatus(sessionId, Status.OPEN);
        }

        return Optional.empty();
    }

    public Carts updateItemQuantity(Long cartId, Long productId, Long variantId, Long newQuantity) {
        Optional<CartItems> cartItems = cartItemsRepository.findByCartIdAndProductIdAndVariantId(
                cartId, productId, variantId
        );

        if (cartItems.isPresent()) {
            CartItems item = cartItems.get();
            item.setQuantity(newQuantity);
            item.setUpdatedAt(LocalDateTime.now());
            cartItemsRepository.save(item);

            // Cập nhật thời gian sửa giỏ hàng
            Optional<Carts> cart = cartsRepository.findById(cartId);
            if (cart.isPresent()) {
                Carts existingCart = cart.get();
                existingCart.setUpdatedAt(LocalDateTime.now());
                return cartsRepository.save(existingCart);
            }
        }
        return null;
    }
    // ============================================================
    // MERGE GIỎ HÀNG (GUEST → USER) KHI LOGIN
    // ============================================================
    public Carts mergeCart(String sessionId, Long userId) {
        // Giỏ guest theo sessionId
        Optional<Carts> guestOpt = cartsRepository
                .findBySessionIdAndStatus(sessionId, Status.OPEN);

        // Giỏ user theo userId
        Optional<Carts> userOpt = cartsRepository
                .findByUserIdAndStatus(userId, Status.OPEN);

        // 1) Không có giỏ guest -> trả về giỏ user (nếu có), đảm bảo sessionId = null
        if (guestOpt.isEmpty()) {
            if (userOpt.isPresent()) {
                Carts userCart = userOpt.get();
                userCart.setSessionId(null);              // QUAN TRỌNG
                userCart.setUpdatedAt(LocalDateTime.now());
                return cartsRepository.save(userCart);
            }
            return null;
        }

        Carts guestCart = guestOpt.get();

        // 2) User chưa có giỏ -> dùng luôn giỏ guest, đổi sang userId + bỏ sessionId
        if (userOpt.isEmpty()) {
            guestCart.setUserId(userId);
            guestCart.setSessionId(null);                 // bỏ sessionId
            guestCart.setUpdatedAt(LocalDateTime.now());
            return cartsRepository.save(guestCart);
        }

        // 3) Cả guestCart & userCart đều có -> merge item, giữ lại giỏ user
        Carts userCart = userOpt.get();

        for (CartItems gItem : guestCart.getItems()) {
            Optional<CartItems> exist = cartItemsRepository
                    .findByCartIdAndProductIdAndVariantId(
                            userCart.getId(),
                            gItem.getProductId(),
                            gItem.getVariantId()
                    );

            if (exist.isPresent()) {
                // Cộng dồn quantity
                CartItems userItem = exist.get();
                userItem.setQuantity(
                        (userItem.getQuantity() == null ? 0L : userItem.getQuantity())
                                + (gItem.getQuantity() == null ? 0L : gItem.getQuantity())
                );
                userItem.setUpdatedAt(LocalDateTime.now());
                cartItemsRepository.save(userItem);
            } else {
                // Chuyển item guest sang giỏ user
                gItem.setCart(userCart);
                gItem.setUpdatedAt(LocalDateTime.now());
                cartItemsRepository.save(gItem);
            }
        }

        // Xóa giỏ guest
        cartsRepository.delete(guestCart);

        // Đảm bảo giỏ user không còn sessionId
        userCart.setSessionId(null);                      // QUAN TRỌNG
        userCart.setUpdatedAt(LocalDateTime.now());
        return cartsRepository.save(userCart);
    }
}
