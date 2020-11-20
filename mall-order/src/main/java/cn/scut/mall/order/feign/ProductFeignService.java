package cn.scut.mall.order.feign;

import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("product/spuinfo/skuId/{skuId}")
    public R getSpuInfoBySkuId(@PathVariable(value = "skuId")Long skuId);
}
