package cn.scut.mall.order.service.impl;

import cn.scut.common.to.mq.OrderTo;
import cn.scut.common.utils.R;
import cn.scut.common.vo.MemberRespVo;
import cn.scut.mall.order.config.AlipayTemplate;
import cn.scut.mall.order.constant.OrderConstant;
import cn.scut.mall.order.dao.OrderItemDao;
import cn.scut.mall.order.entity.OrderItemEntity;
import cn.scut.mall.order.entity.PaymentInfoEntity;
import cn.scut.mall.order.enume.OrderStatusEnum;
import cn.scut.mall.order.exception.NoStockException;
import cn.scut.mall.order.feign.CartFeignService;
import cn.scut.mall.order.feign.MemberFeignService;
import cn.scut.mall.order.feign.ProductFeignService;
import cn.scut.mall.order.feign.WmsFeignService;
import cn.scut.mall.order.interceptor.LoginUserInterceptor;
import cn.scut.mall.order.service.OrderItemService;
import cn.scut.mall.order.service.PaymentInfoService;
import cn.scut.mall.order.to.OrderCreateTo;
import cn.scut.mall.order.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.Query;

import cn.scut.mall.order.dao.OrderDao;
import cn.scut.mall.order.entity.OrderEntity;
import cn.scut.mall.order.service.OrderService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回的数据
     *
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberRespVo member = LoginUserInterceptor.threadLocal.get();//-----------》ThreadLocal共享了上下文信息
        //TODO 这里会遇到Feign 异步情况下 上下文丢失问题
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        //TODO 1 远程查询所有的收货地址列表
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(member.getId());
            orderConfirmVo.setAddress(address);
        }, executor);
        //TODO 2 远程查询购物车所有选中的购物项
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            //TODO 查询商品库存
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skuHasStock = wmsFeignService.getSkuHasStock(skuIds);
            List<SkuStockVo> data = skuHasStock.getData("data", new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                orderConfirmVo.setStocks(map);
            }
        }, executor);
        //TODO 3 查询用户积分
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            Integer integration = member.getIntegration();
            orderConfirmVo.setIntegration(integration);
        }, executor);
        //TODO 4 其他数据自动计算，比如总价格
        //TODO 5 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");//制作令牌
        orderConfirmVo.setOrderToken(token);//令牌返回给页面一个
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + member.getId(), token, 30, TimeUnit.MINUTES);//服务器存一份
        CompletableFuture.allOf(future1, future2, future3).get();
        return orderConfirmVo;
    }

    //本地事务，在分布式系统，只能控制自己的回滚，控制不了其他服务的回滚
    //分布式事务：最大原因。网络问题+分布式机器
    //(isolation = Isolation.REPEATABLE_READ)
//    @Transactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        response.setCode(0);
        submitVoThreadLocal.set(vo);
        MemberRespVo member = LoginUserInterceptor.threadLocal.get();
        //TODO 验证令牌【令牌的对比和删除必须保证原子性】
        String script = "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //返回 0 和 1
        //0 令牌失败  1 删除成功
        Long result = stringRedisTemplate
                .execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + member.getId()),
                        orderToken);
        if (result == 0L) {
            //令牌验证失败
            response.setCode(1);
            return response;
        } else {
            //令牌验证通过
            //TODO 创建订单，验令牌，验价格，锁库存
            OrderCreateTo order = orderCreate();
            //验价
            BigDecimal payAmount = order.getOrder().getPayAmount();//计算的价格
            BigDecimal payPrice = vo.getPayPrice();//页面提交的金额
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {//由于进位 只要求低于小数点两位的误差
                //TODO 对比成功,保存订单信息 到 数据库
                saveOrder(order);
                //TODO 锁定库存--如果锁定失败--》还要取消订单，只要有异常 回滚方法
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLocks(locks);
                //TODO 远程锁库存
                R r = wmsFeignService.orderLockStock(wareSkuLockVo);
                if (r.getCode() == 0) {
                    //锁定成功
                    response.setOrder(order.getOrder());
                    //TODO 5 远程扣减积分，出异常====>订单回滚，库存不回滚
                    //TODO 订单创建成功 发送消息
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    return response;
                } else {
                    //锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);//锁定失败，让订单感知
                }
            } else {
                response.setCode(2);
                return response;
            }
        }
//        String redisToken = stringRedisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + member.getId());
//        if(orderToken!=null && orderToken.equals(redisToken)){
//            //令牌验证通过
//            stringRedisTemplate.delete()
//            return null;
//        }else {
//            return null;
//        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity one = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return one;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        //查询当前订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        //关单
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
            //待付款
            orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(orderEntity);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(entity, orderTo);
            //TODO 订单关闭成功---》立即 发送消息 给 库存那边
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
        }
    }

    /**
     * 获取订单的支付信息
     *
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        PayVo payVo = new PayVo();
        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);//支付宝只支持小数点后两位
        payVo.setBody("订单备注信息-凯凯");//订单的备注
        payVo.setOut_trade_no(orderSn);//订单号
        payVo.setSubject("订单标题信息--凯凯");//订单标题
        payVo.setTotal_amount(bigDecimal.toString());//订单金额
        return payVo;
    }

    /**
     * 查询当前登录用户的所有订单信息
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo member = LoginUserInterceptor.threadLocal.get();
        Long memberId = member.getId();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberId).orderByDesc("id")
        );
        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> orderItemEntities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(orderItemEntities);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(collect);
        return new PageUtils(page);
    }

    /**
     * 处理支付宝的支付结果
     *
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo){
        //1 保存交易流水---一个订单对应一个交易流水信息
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(infoEntity);
        //2 修改 订单的 状态信息
        if (vo.getTrade_status().equals("TRADE_SUCCESS")
                || vo.getTrade_status().equals("TRADE_FINISHED")) {
            String orderSn = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode());

        }
        return "success";
    }

    @Override
    public void createSeckillOrder(OrderService orderService) {
        //TODO 保存订单信息----应该 之前 还有个 确认页
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderEntity.getOrderSn());
    }

    //保存订单到数据库
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }

    //创建订单
    private OrderCreateTo orderCreate() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //1 生成订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity order = buildOrder(orderSn);//构建订单
        orderCreateTo.setOrder(order);
        //2 获取所有的订单项目
        List<OrderItemEntity> orderItems = buildOrderItems(orderSn);//从购物车中查询选中提交的商品
        orderCreateTo.setOrderItems(orderItems);
        //3 计算订单价格信息
        computePrice(order, orderItems);

        return orderCreateTo;
    }

    //计算订单金钱
    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItems) {
        BigDecimal total = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        BigDecimal integrationAmount = new BigDecimal("0");
        Integer integration = 0;
        Integer growth = 0;
        for (OrderItemEntity entity : orderItems) {
            coupon = coupon.add(entity.getCouponAmount());//优惠卷
            promotionAmount = promotionAmount.add(entity.getPromotionAmount());//打折
            integrationAmount = integrationAmount.add(entity.getIntegrationAmount());//积分
            total = total.add(entity.getRealAmount());
            integration = integration + entity.getGiftIntegration();
            growth = growth + entity.getGiftGrowth();

        }
        // 金额信息
        order.setTotalAmount(total);//订单总额（不包含运费）
        order.setPayAmount(total.add(order.getFreightAmount()));//应付总额
        order.setPromotionAmount(promotionAmount);
        order.setIntegrationAmount(integrationAmount);
        //设置积分信息
        order.setCouponAmount(coupon);
        order.setIntegration(integration);
        order.setGrowth(growth);
        order.setDeleteStatus(0);
    }

    //构建订单
    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo member = LoginUserInterceptor.threadLocal.get();
        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(member.getId());
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareVo = fare.getData(new TypeReference<FareVo>() {
        });
        orderEntity.setFreightAmount(fareVo.getFare());//运费信息
        orderEntity.setReceiverProvince(fareVo.getMemberAddressVo().getProvince());//
        orderEntity.setReceiverCity(fareVo.getMemberAddressVo().getCity());//
        orderEntity.setReceiverRegion(fareVo.getMemberAddressVo().getRegion());//
        orderEntity.setReceiverDetailAddress(fareVo.getMemberAddressVo().getDetailAddress());//收货地址信息
        orderEntity.setReceiverPostCode(fareVo.getMemberAddressVo().getPostCode());//
        orderEntity.setReceiverName(fareVo.getMemberAddressVo().getName());//收货人信息
        orderEntity.setReceiverPhone(fareVo.getMemberAddressVo().getPhone());//
        //订单状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

    //构建所有订单项数据
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //TODO 最后一次确认 购物车商品的 价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (CollectionUtils.isNotEmpty(currentUserCartItems)) {
            List<OrderItemEntity> collect = currentUserCartItems.stream().map(careItem -> {
                OrderItemEntity entity = buildOrderItem(careItem);
                entity.setOrderSn(orderSn);
                return entity;
            }).collect(Collectors.toList());
            return collect;
        }

        return null;
    }

    //构建一个订单项
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //1 订单信息，订单号
        //2 商品的SPU信息
        Long skuId = item.getSkuId();
        R spuInfo = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfoData = spuInfo.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoData.getId());
        orderItemEntity.setSpuName(spuInfoData.getSpuName());
        orderItemEntity.setSpuBrand(spuInfoData.getBrandId().toString());
        orderItemEntity.setCategoryId(spuInfoData.getCatalogId());
        //3 商品的SKU信息
        orderItemEntity.setSkuId(item.getSkuId());
        orderItemEntity.setSkuName(item.getTitle());
        orderItemEntity.setSkuPic(item.getImage());
        orderItemEntity.setSkuPrice(item.getPrice());
        orderItemEntity.setSkuAttrsVals(StringUtils.collectionToCommaDelimitedString(item.getSkuAttr()));//这里StringUtils工具类
        orderItemEntity.setSkuQuantity(item.getCount());
        //4 优惠信息--不做
        //5 积分信息
        orderItemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());//这里模拟给 积分 就是价格的整数
        orderItemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount())).intValue());
        //6 订单项的 价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));//实际需要去远程查询
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额
        BigDecimal orign = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        BigDecimal real = orign
                .subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(real);
        return orderItemEntity;
    }
}