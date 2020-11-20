package cn.scut.mall.auth.controller;

import cn.scut.common.constant.AuthServerConstant;
import cn.scut.common.utils.HttpUtils;
import cn.scut.common.utils.R;
import cn.scut.mall.auth.feign.MemberFeignService;
import cn.scut.common.vo.MemberRespVo;
import cn.scut.mall.auth.vo.SocialUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;
import java.net.Socket;
import java.util.HashMap;

/**
 * 处理社交登录
 */
@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    //http://auth.mall.com/oauth2.0/weibo/success?code=e373ce4bb9a12e5ec69c0da6fa87a098
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        //1 根据code 换取 到 codeToken
        HashMap<String, String> map = new HashMap<>();
        map.put("client_id", "1192033175");
        map.put("client_secret", "53e47ff037164e669d3c5945dbbce37e");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.mall.com/oauth2.0/weibo/success");
        map.put("code", code);

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "POST", new HashMap<>(), new HashMap<>(), map);
        if(response.getStatusLine().getStatusCode()==200){
            //获取到 accessToken
            String toString = EntityUtils.toString(response.getEntity());//工具类 将 响应体 内的 entity 转化为 字符串
            SocialUser socialUser = JSON.parseObject(toString, SocialUser.class);
            //知道 当前 是 哪个 社交用户
            //TODO 1）当前用户是第一次进入网站，自动注册进来（为当前社交用户生成一个会员信息账号，以后这个 社交账号 就对应 指定的 会员）
            //登录 或者 注册
            R oauthlogin = memberFeignService.oauthlogin(socialUser);
            if(oauthlogin.getCode()== 0){
                MemberRespVo memberRespVo = oauthlogin.getData(new TypeReference<MemberRespVo>() {});
                System.out.println("登录成功，用户信息是："+memberRespVo);
                //1 第一次使用 session，命令浏览器保存开号，JSESSIONID这个cookie；
                //以后浏览器访问哪个网站就会带上这个网站的cookie；
                //子域之间：mall.com  auth.mall.com order,mall.com
                //发卡的时候：即使时子域系统发的卡，也能让父域直接使用-----》指定域名为父域
                //TODO 1 默认 发的令牌 session=dadadadad 作用域：当前域（解决子域session共享问题）
                //TODO 2 使用JSON的 序列化方式来序列化对象数据到redis
                session.setAttribute(AuthServerConstant.LOGIN_USER,memberRespVo);

                return "redirect:http://mall.com";
            }else {
                return "redirect:http://auth.mall.com/login.htnl";
            }

        }else {
            return "redirect:http://auth.mall.com/login.htnl";
        }
    }

}
