package cn.scut.mall.seckill.service;

import cn.scut.mall.seckill.to.SeckillSkuCacheTo;

import java.util.List;

public interface SeckillService {

    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuCacheTo> getCurrentSeckillSkus();

    SeckillSkuCacheTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
