package cn.scut.mall.seckill.service.impl;

import cn.scut.common.to.mq.SeckillOrderTo;
import cn.scut.common.utils.R;
import cn.scut.common.vo.MemberRespVo;
import cn.scut.mall.seckill.contant.SeckillConstant;
import cn.scut.mall.seckill.feign.CouponFeignService;
import cn.scut.mall.seckill.feign.ProductFeignService;
import cn.scut.mall.seckill.interceptor.LoginUserInterceptor;
import cn.scut.mall.seckill.service.SeckillService;
import cn.scut.mall.seckill.to.SeckillSkuCacheTo;
import cn.scut.mall.seckill.vo.SeckillSessionsWithSkus;
import cn.scut.mall.seckill.vo.SkuInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1 去扫描需要参加秒杀的活动
        R r = couponFeignService.getLates3DaySession();
        if (r.getCode() == 0) {
            //上架商品
            List<SeckillSessionsWithSkus> data = r.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            if (CollectionUtils.isNotEmpty(data)) {
                //缓存到redis
                //  1 缓存 活动信息
                saveSessionInfos(data);
                //  2 缓存 活动的关联商品信息
                saveSessionSkuInfos(data);
            }
        }
    }

    /**
     * 去 redis 中 查询 当前时间 下 秒杀商品的 信息
     *
     * @return
     */
    @Override
    public List<SeckillSkuCacheTo> getCurrentSeckillSkus() {
        //1 确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = stringRedisTemplate.keys(SeckillConstant.SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            //seckill:sessions:1603900800000_1603976400000
            String[] s = key.split(":")[2].split("_");
            Long start = Long.parseLong(s[0]);
            Long end = Long.parseLong(s[1]);
            if (time >= start && time <= end) {
                //2 获取 这个秒杀场次需要的所有商品
                List<String> range = stringRedisTemplate.opsForList().range(key, -1, -1);//取出全部的
                BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);
                if (CollectionUtils.isNotEmpty(list)) {
                    List<SeckillSkuCacheTo> collect = list.stream().map(item -> {
                        SeckillSkuCacheTo skuCacheTo = JSON.parseObject(item, SeckillSkuCacheTo.class);
                        return skuCacheTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
            }
        }
        return null;
    }

    @Override
    public SeckillSkuCacheTo getSkuSeckillInfo(Long skuId) {
        //1 找到所有需要参与秒杀商品的key
        BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
        Set<String> keys = ops.keys();
        if (CollectionUtils.isNotEmpty(keys)) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String s = ops.get(key);
                    SeckillSkuCacheTo skuCacheTo = JSON.parseObject(s, SeckillSkuCacheTo.class);
                    //TODO 对随机码处理 ，如果当前商品处于秒杀时间内，才给页面 随机码，否则不给
                    long now = new Date().getTime();
                    if (now >= skuCacheTo.getStartTime() || now <= skuCacheTo.getEndTime()) {
                    } else {
                        skuCacheTo.setRandonmCode(null);
                    }
                    return skuCacheTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo member = LoginUserInterceptor.threadLocal.get();
        //TODO  校验合法性 1 秒杀时间 2 随机码 3 对应关系 4 幂等性
        BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
        // 获取 商品信息
        String s = ops.get(killId);
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            SeckillSkuCacheTo skuCacheTo = JSON.parseObject(s, SeckillSkuCacheTo.class);
            if (num > skuCacheTo.getSeckillLimit().intValue()) {//数量限制
                return null;
            }
            //校验时间的合法性
            Long endTime = skuCacheTo.getEndTime();
            Long startTime = skuCacheTo.getStartTime();
            long time = new Date().getTime();
            if ((time < startTime) || (time > endTime)) {
                return null;
            }
            // 校验随机码 和 商品id
            String randonmCode = skuCacheTo.getRandonmCode();
            String skuIdSession = skuCacheTo.getPromotionSessionId()+"_"+skuCacheTo.getSkuId();
            if ((!randonmCode.equals(key) || !skuIdSession.equals(killId)) ){
                return null;
            }
            //这个人是否已经购买过
            //TODO 幂等性：只要秒杀成功，就去占位 userId_skuId_sessionId
            //让他 自动过期
            String redisKey = member.getId() + "_" + skuIdSession;
            Long ttl = (endTime - startTime);
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);//setNx
            if (!flag) return null;//买过了
            // 终于可以 去 抢购了 TODO ---》分布式信号量
            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + key);
//            try {
//                semaphore.acquire(num);//这种方法会阻塞
//            } catch (InterruptedException e) {
//
//            }
            try {
                semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);//100毫秒拿不到就算了
                //TODO 秒杀成功 快速下单--》发送MQ消息
                String orderSn = IdWorker.getTimeId();
                SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                seckillOrderTo.setMemberId(member.getId());
                seckillOrderTo.setOrderSn(orderSn);
                seckillOrderTo.setNum(num);
                seckillOrderTo.setPromotionSessionId(skuCacheTo.getPromotionSessionId());
                seckillOrderTo.setSeckillPrice(skuCacheTo.getSeckillPrice());
                seckillOrderTo.setSkuId(skuCacheTo.getSkuId());
                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",seckillOrderTo);
                return orderSn;
            } catch (InterruptedException e) {
                return null;
            }
        }
    }

    //一个List 表示 这一场秒杀活动
    //key记录秒杀活动的开始和结束时间
    //value记录秒杀活动的商品的skuId List集合
    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            long start = session.getStartTime().getTime();
            long end = session.getEndTime().getTime();
            String key = SeckillConstant.SESSIONS_CACHE_PREFIX + start + "_" + end;
            if (!stringRedisTemplate.hasKey(key)) {
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                stringRedisTemplate.opsForList().leftPushAll(key, collect);
            }

        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.stream().forEach(session -> {
            //准备hash操作
            BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SeckillConstant.SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {

                if (!ops.hasKey(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId().toString())) {
                    //由于页面需要展示sku的具体数据
                    SeckillSkuCacheTo cacheTo = new SeckillSkuCacheTo();
                    //1 sku 的 基本数据
                    R info = productFeignService.info(seckillSkuVo.getSkuId());
                    if (info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        cacheTo.setSkuInfoVo(skuInfo);
                    }
                    //2 sku 的 秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, cacheTo);
                    //3 设置上当前商品的秒杀时间
                    cacheTo.setStartTime(session.getStartTime().getTime());
                    cacheTo.setEndTime(session.getEndTime().getTime());
                    //4 商品的随机码？ seckill?skuId = 41 & key = adadasda
                    //TODO 保护机制随机码在 秒杀的时候才会暴露 ，防止 你一直 请求攻击
                    String token = UUID.randomUUID().toString().replace("-", "");
                    cacheTo.setRandonmCode(token);

                    //5 使用库存作为分布式的信号量--》限流
                    String string = JSON.toJSONString(cacheTo);
                    ops.put(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId().toString(), string);
                    //TODO 引入分布式的信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                }

            });
        });
    }
}
