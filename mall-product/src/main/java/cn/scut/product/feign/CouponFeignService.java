package cn.scut.product.feign;

import cn.scut.common.to.SkuReductionTo;
import cn.scut.common.to.SpuBoundTo;
import cn.scut.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-coupon")
public interface CouponFeignService {
    @GetMapping("coupon/coupon/member/list")
    public R memberCoupons();

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("/coupon/skufullreduction/saveinfo")
    public R saveInfo(@RequestBody SkuReductionTo skuReductionTo);
}
