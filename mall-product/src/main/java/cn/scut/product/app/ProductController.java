package cn.scut.product.app;

import cn.scut.common.util.R;
import cn.scut.product.feign.CouponFeignService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "商品 controller")
@RestController
@RequestMapping("product/")
public class ProductController {

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/coupon")
    public R test(){
        R r = couponFeignService.memberCoupons();
        return r;
    }
}
