package cn.scut.mall.product.feign.fallback;

import cn.scut.common.exception.BizCodeEnume;
import cn.scut.common.utils.R;
import cn.scut.mall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {
    @Override
    public R skuSeckillInfo(Long skuId) {
        log.info("skuSeckillInfo熔断方法调用！");
        return R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(),BizCodeEnume.TOO_MANY_REQUEST.getMessage());
    }
}
