package cn.scut.mall.ware.feign;


import cn.scut.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-member")
public interface MemberFeignService {
    @RequestMapping("member/memberreceiveaddress/info/{id}")
    public R info(@PathVariable("id") Long id);
}
