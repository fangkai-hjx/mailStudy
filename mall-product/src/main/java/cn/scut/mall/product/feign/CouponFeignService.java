package cn.scut.mall.product.feign;

import cn.scut.common.to.SkuReductionTo;
import cn.scut.common.to.SpuBoundTo;
import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 1 CouponFeignService.saveSpuBounds(spuBoundTo)
     *      1）@RequestBody将这个对象转化为 json
     *      2）找到“mall-coupon”服务，给/coupon/spubounds/save发送请求。
     *          将上一步转的json放在请求体的位置，发送请求
     *      3）对方服务收到请求。请求体里的json数据
     *          (@RequestBody SpuBoundsEntity spuBounds)：将请求体的json转化为SpuBoundsEntity；
     *   总结：只要json数据模型是兼容的。双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
