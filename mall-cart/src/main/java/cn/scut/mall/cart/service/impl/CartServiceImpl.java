package cn.scut.mall.cart.service.impl;

import cn.scut.common.constant.CartConstant;
import cn.scut.common.utils.R;
import cn.scut.mall.cart.feign.ProductFeignService;
import cn.scut.mall.cart.interceptor.CartInterceptor;
import cn.scut.mall.cart.service.CartService;
import cn.scut.mall.cart.vo.Cart;
import cn.scut.mall.cart.vo.CartItem;
import cn.scut.mall.cart.vo.SkuInfoVo;
import cn.scut.mall.cart.vo.UserInfoTo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        String o = (String) operations.get(skuId.toString());
        if (StringUtils.isEmpty(o)) {
            // 购物车无此商品
            //1 远程查询当前要添加的商品信息
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> futureTask1 = CompletableFuture.runAsync(() -> {
                R skuR = productFeignService.info(skuId);
                SkuInfoVo skuInfo = skuR.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //2 新的商品添加到购物车---》添加新商品
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(skuInfo.getPrice());
            });
            CompletableFuture<Void> futureTask2 = CompletableFuture.runAsync(() -> {
                //3 远程查询sku组合信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(skuSaleAttrValues);
            });
            CompletableFuture.allOf(futureTask1, futureTask2).get();

            String jsonString = JSON.toJSONString(cartItem);
            operations.put(skuId.toString(), jsonString);
            return cartItem;
        } else {
            //购物车有该商品，修改数量
            CartItem cartItem = JSON.parseObject(o, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            String jsonString = JSON.toJSONString(cartItem);
            operations.put(skuId.toString(), jsonString);//重写放回redis
            return cartItem;
        }
    }

    /**
     * 获取购物车的某个购物项
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cart = getCartOps();
        String o = (String) cart.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        //1 登录
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {//登录
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            //2 如果临时购物车的数据需要合并
            List<CartItem> cartItems = getCartItems(cartKey);
            if (!CollectionUtils.isEmpty(cartItems)) {//临时购物车有数据
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                //清除购物车
                clearCart(cartKey);
            }
            //3 获取登录后的购物车数据
            List<CartItem> cartItems1 = getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserId());
            cart.setItems(cartItems1);
        } else {//没登陆
            //获取临时购物车的所有购物车
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();//拿所有的值
        if (!CollectionUtils.isEmpty(values)) {
            List<CartItem> collect = values.stream().map(obj -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        ;
        return null;
    }

    /**
     * 清空购物车
     *
     * @param cartKey
     */
    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck((check == 1) ? true : false);
        String str = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), str);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String str = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), str);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        System.out.println("getCurrentUserCartItems主线程"+Thread.currentThread());
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null ) {
            return null;
        } else {
            List<CartItem> cartItems = getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserId());//这是所有购物项
            //获取被选中的购物项
            List<CartItem> collect = cartItems.stream()
                    .map(item->{
                        //TODO 获取最新的价格--比如你一年前放在购物车的商品，现在价格肯定不一样了
                        BigDecimal newPrice = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(newPrice);
                        return item;
                    })
                    .filter(item -> item.getCheck() == true).collect(Collectors.toList());
            return collect;
        }

    }

    /**
     * 获取需要的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //1，
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {//登录了
            //mall:cart:1
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            //mall:cart:dadadadadada
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        //如果购物车有了 这个商品--->修改数量
//        Object o = redisTemplate.opsForHash().get(cartKey, "1");
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
