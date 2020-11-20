package cn.scut.mall.product.feign;

import cn.scut.common.utils.R;
import cn.scut.mall.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(value = "mall-seckill",fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    R skuSeckillInfo(@PathVariable(value = "skuId",required = true)Long skuId);
}
