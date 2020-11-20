package cn.scut.mall.ware.feign;

import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-order")
public interface OrderFeignService {

    @RequestMapping("order/order/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
