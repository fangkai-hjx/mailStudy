package cn.scut.cart.service;

import cn.scut.cart.vo.Cart;
import cn.scut.cart.vo.CartItem;

public interface CartService {
    CartItem addToCart(Long skuId,Integer num);

    CartItem getCartItem(Long skuId);

    Cart getCart();

    void deleteCart(String cartKey);

    void checkItem(Long skuId, Integer check);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
