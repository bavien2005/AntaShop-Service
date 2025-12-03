package org.anta.cart_service.service;


import lombok.RequiredArgsConstructor;
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
public class CartsService {
    private final CartsRepository cartsRepository;
    private final CartItemsRepository cartItemsRepository;

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
    public Carts AddItemsToCarts(CartItemsRequest req){
        Optional<Carts> optionalCarts;
        if ((req.getUserId()) != null) {
            optionalCarts = cartsRepository.findByUserIdAndStatus(req.getUserId(), Status.OPEN);
        } else {
            optionalCarts = cartsRepository.findBySessionIdAndStatus(req.getSessionId(), Status.OPEN);
        }

        Carts cart = optionalCarts.orElseGet(() -> createNewCart(req));

        Optional<CartItems> cartItems=cartItemsRepository.findByCartIdAndProductIdAndVariantId(
                cart.getId(), req.getProductId(), req.getVariantId()
        );

        CartItems items;

        if(cartItems.isPresent()){
            items = cartItems.get();
            items.setQuantity(
                    (items.getQuantity() == null ? 0 : items.getQuantity()) + req.getQuantity()
            );
            items.setUpdatedAt(LocalDateTime.now()); // FIX: Thêm updatedAt
        } else {
            items = new CartItems();
            items.setCart(cart);
            items.setProductId(req.getProductId());
            items.setVariantId(req.getVariantId());
            items.setProductName(req.getProductName());
            items.setQuantity(req.getQuantity());
            items.setUnitPrice(req.getUnitPrice());
            items.setCreatedAt(LocalDateTime.now());
            items.setUpdatedAt(LocalDateTime.now());
        }

        cartItemsRepository.save(items);

        // FIX: Cập nhật updatedAt cho cart
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
            return cartsRepository.findByUserIdAndStatus(userId, Status.OPEN);
        }
        return cartsRepository.findBySessionIdAndStatus(sessionId, Status.OPEN);
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

        Optional<Carts> guestOpt = cartsRepository.findBySessionIdAndStatus(sessionId, Status.OPEN);
        Optional<Carts> userOpt = cartsRepository.findByUserIdAndStatus(userId, Status.OPEN);

        // Nếu guest không có giỏ → trả giỏ user (nếu có)
        if (guestOpt.isEmpty()) {
            return userOpt.orElse(null);
        }

        Carts guestCart = guestOpt.get();

        // Nếu user chưa có giỏ → chuyển giỏ session thành giỏ user
        if (userOpt.isEmpty()) {
            guestCart.setUserId(userId);
            guestCart.setSessionId(null);
            guestCart.setUpdatedAt(LocalDateTime.now());
            return cartsRepository.save(guestCart);
        }

        // User có giỏ → MERGE
        Carts userCart = userOpt.get();

        for (CartItems gItem : guestCart.getItems()) {

            Optional<CartItems> exist = cartItemsRepository
                    .findByCartIdAndProductIdAndVariantId(
                            userCart.getId(),
                            gItem.getProductId(),
                            gItem.getVariantId()
                    );

            if (exist.isPresent()) {
                CartItems userItem = exist.get();
                userItem.setQuantity(userItem.getQuantity() + gItem.getQuantity());
                userItem.setUpdatedAt(LocalDateTime.now());
                cartItemsRepository.save(userItem);

            } else {
                gItem.setCart(userCart);
                gItem.setUpdatedAt(LocalDateTime.now());
                cartItemsRepository.save(gItem);
            }
        }

        cartsRepository.delete(guestCart);
        userCart.setUpdatedAt(LocalDateTime.now());
        return cartsRepository.save(userCart);
    }
}
