package cn.scut.mall.ware.feign;


import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * 第一种：
     *      直接 给后台服务 发请求
     *      @FeignClient("mall-product")   /product/skuinfo//info/{skuId}
     * 第二种:
     *      让所有请求过网关
     *      @FeignClient("mall-geteway")   /api/product/skuinfo//info/{skuId}
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo//info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
