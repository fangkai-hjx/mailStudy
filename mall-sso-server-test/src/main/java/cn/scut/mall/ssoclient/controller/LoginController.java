package cn.scut.mall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token) {
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }

    /**
     * 登录成功 跳转 到 之前 的 地址---》从哪儿来 到哪儿去
     *
     * @return
     */
    @PostMapping("/doLogin")
    public String hello(@RequestParam String username, String password, String url, Model model, HttpServletResponse response) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {//登录成功
            System.out.println("username:" + username + "---" + "password:" + password);
            //把登录信息
            String uuid = UUID.randomUUID().toString().replace("-", "");
            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);
            redisTemplate.opsForValue().set(uuid, username);
            return "redirect:" + url + "?token=" + uuid;
        }
        model.addAttribute("url", url);
        return "login";
    }

    @GetMapping("/login.html")
    public String login(@RequestParam("redirect_url") String url, Model model, @CookieValue(value = "sso_token", required = false) String sso_token) {
        if (!StringUtils.isEmpty(sso_token)) {
            //说明之前有人登录过，留下了cookie
            return "redirect:" + url + "?token=" + sso_token;
        }
        model.addAttribute("url", url);
        return "login";
    }
}
