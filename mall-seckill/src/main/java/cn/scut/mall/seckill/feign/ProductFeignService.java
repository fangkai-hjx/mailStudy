package cn.scut.mall.seckill.feign;


import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@FeignClient("mall-product")
public interface ProductFeignService{

    @GetMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
