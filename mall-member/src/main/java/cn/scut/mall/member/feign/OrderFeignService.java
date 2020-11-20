package cn.scut.mall.member.feign;

import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("mall-order")
public interface OrderFeignService {

    @GetMapping("order/order/listWithItem")
    public R listWithItem(@RequestParam Map<String, Object> params);
}
