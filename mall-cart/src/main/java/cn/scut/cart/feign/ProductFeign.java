package cn.scut.cart.feign;

import cn.scut.common.util.R;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@FeignClient("mall-product")
public interface ProductFeign {

    @RequestMapping("product/skuinfo/info/{id}")
    R info(@PathVariable("id") Long id);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);
}
