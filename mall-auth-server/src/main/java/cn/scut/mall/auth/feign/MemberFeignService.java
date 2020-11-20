package cn.scut.mall.auth.feign;

import cn.scut.common.utils.R;
import cn.scut.mall.auth.vo.SocialUser;
import cn.scut.mall.auth.vo.UserLoginVo;
import cn.scut.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    public R regist(@RequestBody UserRegisterVo vo);

    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    public R oauthlogin(@RequestBody SocialUser vo) throws Exception;
}
