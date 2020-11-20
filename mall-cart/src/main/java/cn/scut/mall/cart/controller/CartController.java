package cn.scut.mall.cart.controller;

import cn.scut.mall.cart.interceptor.CartInterceptor;
import cn.scut.mall.cart.service.CartService;
import cn.scut.mall.cart.vo.Cart;
import cn.scut.mall.cart.vo.CartItem;
import cn.scut.mall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){
        System.out.println("Controller的线程"+Thread.currentThread());
        List<CartItem> list = cartService.getCurrentUserCartItems();
        return list;
    }


    @GetMapping("/deleteItem.html")
    public String countItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.mall.com/cart.html";
    }
    @GetMapping("/countItem.html")
    public String countItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.mall.com/cart.html";
    }
    @GetMapping("/checkItem.html")
    public String checkItem(@RequestParam("skuId")Long skuId,
                            @RequestParam("check")Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.mall.com/cart.html";
    }
    /**
     * 浏览器有一个cookie：user-key：表示用户的身份
     * 如果第一次使用jd购物车，都会给一个临时的用户身份
     * 浏览器以后保存，每次访问保存
     *
     * 登录：session有
     * 没登录：cookie中携带的user-key
     * 第一次 如果没有零时用户，帮忙传接一个临时用户。
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //快速得到 用户 信息
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addTocart")
    public String addTocart(@RequestParam("skuId")Long skuId,
                            @RequestParam("num")Integer num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        attributes.addAttribute("skuId",skuId);//请求参数携带skuId
        return "redirect:http://cart.mall.com/addTocartSuccess.html";//跳转到购物成功页 防止刷新重复增加购物车
    }

    @GetMapping("/addTocartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId")Long skuId,Model model){
        //重定向到成功页面 ，再次查询购物车数据
        CartItem cartItem  = cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }
}
