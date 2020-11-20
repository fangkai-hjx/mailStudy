package cn.scut.mall.order.feign;

import cn.scut.mall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/member/{memberId}/addresses")
    public List<MemberAddressVo> getAddress(@PathVariable(name = "memberId") Long memberId);
}
