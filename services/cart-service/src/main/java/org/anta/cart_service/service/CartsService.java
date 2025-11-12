package org.anta.cart_service.service;


import lombok.RequiredArgsConstructor;
import org.anta.cart_service.dto.request.CartItemsRequest;
import org.anta.cart_service.entity.CartItems;
import org.anta.cart_service.entity.Carts;
import org.anta.cart_service.enums.Status;
import org.anta.cart_service.repository.CartItemsRepository;
import org.anta.cart_service.repository.CartsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartsService {
    private final CartsRepository cartsRepository;
    private final CartItemsRepository cartItemsRepository;


    //them sp vao gio
    public Carts AddItemsToCarts(CartItemsRequest req){
        Optional<Carts> optionalCarts;
        if ((req.getUserId()) != null) {
            optionalCarts = cartsRepository.findByUserIdAndStatus(req.getUserId(), Status.OPEN);
        } else {
            optionalCarts = cartsRepository.findBySessionIdAndStatus(req.getSessionId(), Status.OPEN);
        }

        Carts cart = optionalCarts.orElseGet(() -> createNewCart(req));

        Optional<CartItems> cartItems=cartItemsRepository.findByCartIdAndProductIdAndVariantId(cart.getId(), req.getProductId(), req.getVariantId());

        CartItems items;

        if(cartItems.isPresent()){
            items = cartItems.get();
            items.setQuantity(
                    (items.getQuantity() == null ? 0 : items.getQuantity()) + req.getQuantity()
            );
        }else{
            items=new CartItems();
            items.setCart(cart);
            items.setProductId(req.getProductId());
            items.setVariantId(req.getVariantId());
            items.setProductName(req.getProductName());
            items.setQuantity(req.getQuantity());
            items.setUnitPrice(req.getUnitPrice());

        }

        cartItemsRepository.save(items);
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

}
