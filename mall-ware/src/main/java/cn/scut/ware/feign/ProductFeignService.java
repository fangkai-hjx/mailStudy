package cn.scut.ware.feign;

import cn.scut.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//TODO 不过网关
@FeignClient("mall-product")
public interface ProductFeignService {
    @GetMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
//TODO 过网关
//@FeignClient("mall-gateway")
//public interface ProductFeignService {
//    @GetMapping("/api/product/skuinfo/info/{skuId}")
//    public R info(@PathVariable("skuId") Long skuId);
//}
