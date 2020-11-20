package cn.scut.mall.seckill.Scheduled;

import cn.scut.mall.seckill.contant.SeckillConstant;
import cn.scut.mall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品的定时上架
 *      每天晚上3点：上架最近三天需要秒杀的商品。
 *      当天 00：00：00 - 23：59：59
 *      明天 00：00：00 - 23：59：59
 *      后天 00：00：00 - 23：59：59
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    //TODO 幂等性处理---l两个方面来考虑，第一个是同一个服务开多份，每次执行一个就行，第二个幂等性
    @Scheduled(cron = "*/3 * * * * ?")//秒分时日月周
    public void uploadSeckillSkuLatest3Days(){
        //1 重复上架无需处理
        log.info("上架秒杀的商品信息");
        RLock lock = redissonClient.getLock(SeckillConstant.UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
