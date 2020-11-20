package cn.scut.mall.auth.controller;

import cn.scut.common.constant.AuthServerConstant;
import cn.scut.common.exception.BizCodeEnume;
import cn.scut.common.utils.R;
import cn.scut.common.vo.MemberRespVo;
import cn.scut.mall.auth.feign.MemberFeignService;
import cn.scut.mall.auth.feign.ThridPartyFeignService;
import cn.scut.mall.auth.vo.UserLoginVo;
import cn.scut.mall.auth.vo.UserRegisterVo;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    ThridPartyFeignService thridPartyFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;


    @GetMapping(value = {"/login.html","/"})
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute == null){//没登录
            return "login";
        }//登录过
        return "redirect:http://fkmall.shop";//这里最好 抽取 为 常量
    }

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {

        //2 验证码的再次校验-----存起来-----redis--{key：phone,value:code}
        //TODO 1 接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotEmpty(redisCode)) {
            long before = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - before < 60000) {//这不就是云计算老师说的那个方法码
                //60s后不能多次发送
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMessage());
            }
        }
        String true_code = UUID.randomUUID().toString().substring(0, 5);
        String code = true_code + "_" + System.currentTimeMillis();//存入上一次的时间
        thridPartyFeignService.sendCode(phone, true_code);
        //redis缓存验证码，防止同一个phone在60s内再次发验证码
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        return R.ok();
    }

    //TODO 重定向携带数据，利用session原理，将数据放在session中，只要跳转到下一个页面去除这个数据后，session里面的数据就会删除
    //TODO 1 分布式下的session问题。
    @PostMapping("/regist")
    public String register(/*@Valid*/ UserRegisterVo vo, BindingResult result, Model model,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {//重定向的数据存放
        if (result.hasErrors()) {
            Map<String, String> errors = result
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(fieldError -> fieldError.getField(), fieldError -> fieldError.getDefaultMessage()));//《出错字段，出错信息》

//            model.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors", errors);//取一次
//            session.setAttribute();
            return "redirect:http://auth.fkmall.shop/reg.html";//.HttpRequestMethodNotSupportedException: Request method 'POST' not supported]
        }
        //1校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.isNotEmpty(s)) {//缓存中存在验证码---》有验证码且未过期
            if (code.equals(s.split("_")[0])) {
                //删除验证码:类似令牌机制
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //验证码校验成功---》调用远程服务 注册
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {//成功

                    return "redirect:http://auth.fkmall.shop/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.fkmall.shop/reg.html";
                }
            }
        }
        //1 缓存不存在验证码  2 验证码比对失败
        Map<String, String> errors = new HashMap<>();
        errors.put("code", "验证码错误");
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.fkmall.shop/reg.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {//注意，前台提交的 不是 json，而是key value 结构的数据
        // TODO 远程登录
        try {
            R r = memberFeignService.login(vo);
            if (r.getCode() == 0) {
                //成功
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                session.setAttribute(AuthServerConstant.LOGIN_USER, data);//登录成功 信息 放到session中
                return "redirect:http://fkmall.shop";
            } else {
                Map<String, String> errors = new HashMap<>();
                String msg = r.getData("msg", new TypeReference<String>() {
                });
                errors.put("msg", msg);
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.fkmall.shop/login.html";
            }
        } catch (Exception e) {//远程调用失败
            return "redirect:http://auth.fkmall.shop/login.html";
        }
    }
}
