package cn.scut.cart.service.impl;

import cn.scut.cart.feign.ProductFeign;
import cn.scut.cart.interceptor.CartInterceptor;
import cn.scut.cart.service.CartService;
import cn.scut.cart.to.UserInfoTo;
import cn.scut.cart.vo.Cart;
import cn.scut.cart.vo.CartItem;
import cn.scut.cart.vo.SkuInfoVo;
import cn.scut.common.constant.CartConstant;
import cn.scut.common.util.R;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private ThreadPoolExecutor threadPool;

    /**
     * 将商品添加到购物车
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        final BoundHashOperations<String, Object, Object> ops = getCartList();
        final String strCartItem = (String) ops.get(skuId.toString());
        if (StringUtils.isEmpty(strCartItem)) {//购物车无此商品
            //1 远程查询当前添加的课程信息
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
                final R r = productFeign.info(skuId);
                final SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                //2 课程详情加入到购物项目
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);//默认被选中
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setPrice(skuInfo.getPrice());
            }, threadPool);
            //3 远程查询sku的组合信息
            CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
                final List<String> saleAttrValues = productFeign.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(saleAttrValues);
            }, threadPool);
            try {
                CompletableFuture.allOf(task1, task2).get();
                final String jsonString = JSON.toJSONString(cartItem);
                ops.put(skuId.toString(), jsonString);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return cartItem;
        } else {//购车车有此商品,修改数量
            CartItem cartItem = JSON.parseObject(strCartItem, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            final String jsonString = JSON.toJSONString(cartItem);
            ops.put(skuId.toString(), jsonString);
            return cartItem;
        }
    }

    /**
     * 跳转到成功页
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        final BoundHashOperations<String, Object, Object> ops = getCartList();
        final String str = (String) ops.get(skuId.toString());
        final CartItem cartItem = JSON.parseObject(str, CartItem.class);
        return cartItem;
    }

    /**
     * 获取整个购物车
     *
     * @return
     */
    @Override
    public Cart getCart() {
        final Cart cart = new Cart();
        //1 区分登录或者未登录
        final UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {//登录了
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            String tempKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);
            //TODO 如果临时购物车数据还没有j进行合并
            final List<CartItem> tempCartItems = getCartItems(tempKey);
            if (tempCartItems != null) {
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                deleteCart(tempKey);//删除临时购物车的数据
            }
            //3 获取登录后的购物车，包含合并过来的临时购物车数据
            final List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        } else {
            final String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            final List<CartItem> cartItems = getCartItems(cartKey);
            if (cartItems != null) cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void deleteCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        final BoundHashOperations<String, Object, Object> ops = getCartList();
        final CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck((check == 1) ? true : false);
        final String jsonString = JSON.toJSONString(cartItem);
        ops.put(skuId.toString(),jsonString);
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        final BoundHashOperations<String, Object, Object> ops = getCartList();
        final CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        final String str = JSON.toJSONString(cartItem);
        ops.put(skuId.toString(),str);
    }

    @Override
    public void deleteItem(Long skuId) {
        final BoundHashOperations<String, Object, Object> cartList = getCartList();
        cartList.delete(skuId.toString());
    }

    private BoundHashOperations<String, Object, Object> getCartList() {
        final UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {//登录了
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        //之前已经有这个商品了
        final BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);//后续操作针对的是这个key
        return ops;
    }

    private List<CartItem> getCartItems(String cartKey) {
        final BoundHashOperations<String, Object, Object> cartListOps = redisTemplate.boundHashOps(cartKey);
        final List<Object> values = cartListOps.values();
        if (!CollectionUtils.isEmpty(values)) {
            final List<CartItem> items = values.stream().map(item -> {
                String str = (String) item;
                final CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return items;
        }
        return null;
    }
}
