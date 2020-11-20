package cn.scut.mall.cart.feign;

import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    @GetMapping("product/skusaleattrvalue//stringlist/{skuId}")
    public List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

    @GetMapping("product/skuinfo/{skuId}/price")
    public BigDecimal getPrice(@PathVariable("skuId")Long skuId);
}
