package cn.scut.mall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.scut.common.exception.BizCodeEnume;
import cn.scut.mall.member.entity.MemberReceiveAddressEntity;
import cn.scut.mall.member.exception.PhoneExistException;
import cn.scut.mall.member.exception.UsernameExistException;
import cn.scut.mall.member.feign.CouponFeignService;
import cn.scut.mall.member.service.MemberReceiveAddressService;
import cn.scut.mall.member.vo.MemberLoginVo;
import cn.scut.mall.member.vo.MemberRegistVo;
import cn.scut.mall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.scut.mall.member.entity.MemberEntity;
import cn.scut.mall.member.service.MemberService;
import cn.scut.common.utils.PageUtils;
import cn.scut.common.utils.R;



/**
 * 会员
 *
 * @author fangkai
 * @email 837220583@qq.com
 * @date 2020-09-06 12:27:55
 */
@RestController
@RequestMapping("/member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    MemberReceiveAddressService memberReceiveAddressService;

    @GetMapping("/{memberId}/addresses")
    public List<MemberReceiveAddressEntity> getAddress(@PathVariable(name = "memberId") Long memberId) {
        List<MemberReceiveAddressEntity> addresses = memberReceiveAddressService.getAddress(memberId);
        return addresses;
    }

    /**
     * 社交登录
     * @param vo
     * @return
     */
    @PostMapping("/oauth2/login")
    public R oauthlogin(@RequestBody SocialUser vo) throws Exception {//接收json数据
        MemberEntity entity = memberService.login(vo);
        if(entity != null){
            return R.ok().put("data",entity);
        }
        return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
    }
    @PostMapping("/login")
    public R login(@RequestBody  MemberLoginVo vo){//接收json数据
        MemberEntity entity = memberService.login(vo);
        if(entity != null){
            return R.ok().put("data",entity);
        }
       return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
    }

    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo vo){//RequestBody 就会POST请求 携带的 json 转化为 对象
        try{
            memberService.regist(vo);
        }catch (PhoneExistException e1){
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(),BizCodeEnume.PHONE_EXIST_EXCEPTION.getMessage());
        }catch (UsernameExistException e2){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(),BizCodeEnume.USER_EXIST_EXCEPTION.getMessage());
        }
        return R.ok();
    }

    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");;
        R membercoupons = couponFeignService.membercoupons();
        return R.ok(membercoupons).put("member",memberEntity).put("coupons",membercoupons.get("coupons"));//前面是本地  后面是远程调用
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
