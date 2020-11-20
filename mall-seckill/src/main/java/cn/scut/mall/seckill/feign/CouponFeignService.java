package cn.scut.mall.seckill.feign;

import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    @GetMapping("coupon/seckillsession/lates3DaySession")
    public R getLates3DaySession();
}
