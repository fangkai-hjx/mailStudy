package cn.scut.mall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoserver;

    /**
     * 无需登录就可访问
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 无需登录就可访问
     *
     * @return
     */
    @GetMapping("/employees")
    public String employees(Model model, HttpSession session, @RequestParam(value = "token", required = false) String token) {
        if (!StringUtils.isEmpty(token)) {
            //去ssoserver登录成功调回来的时候才会带上 token
            //TODO 去ssoserver获取当前token正在对应的用户信息
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> entity = restTemplate.getForEntity("http://ssoserver.com:8080/userInfo?token=" + token, String.class);
            String body = entity.getBody();
            session.setAttribute("loginUser",body);
        }
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser != null) {
            List<String> list = new ArrayList<>();
            list.add("张三");
            list.add("李四");
            list.add("王五");
            model.addAttribute("emps", list);
            return "list";
        } else {
            //没登录 跳转到登录服务器
            return "redirect:" + ssoserver + "?redirect_url=http://client1.com:8081/employees";
        }
    }

}