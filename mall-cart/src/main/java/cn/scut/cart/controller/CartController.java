package cn.scut.cart.controller;

import cn.scut.cart.interceptor.CartInterceptor;
import cn.scut.cart.service.CartService;
import cn.scut.cart.to.UserInfoTo;
import cn.scut.cart.vo.Cart;
import cn.scut.cart.vo.CartItem;
import cn.scut.common.constant.CartConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    StringRedisTemplate redisTemplate;


    @GetMapping("/cart.html")
    String cartListPage(Model model){
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * redis数据结构：cart:uId:
     *
     * @return
     */
    @GetMapping("/addTocart")
    public String addToCart(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer number,
                            RedirectAttributes redirectAttributes){
        cartService.addToCart(skuId, number);
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.fkmall.shop/addToCartSuccess.html";
    }
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,Model model){
        //重定向到成功y页面，再次查询购物车数据即可
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item",item);
        return "success";
    }

    @GetMapping("/checkItem.html")
    public String checkItem(@RequestParam("skuId")Long skuId,@RequestParam("check")Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.fkmall.shop/cart.html";//重定向到购物车列表页，相当于刷新一遍
    }
    @GetMapping("/countItem.html")
    public String countItem(@RequestParam("skuId")Long skuId,@RequestParam("num")Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.fkmall.shop/cart.html";//重定向到购物车列表页，相当于刷新一遍
    }
    @GetMapping("/deleteItem.html")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.fkmall.shop/cart.html";//重定向到购物车列表页，相当于刷新一遍
    }
}
