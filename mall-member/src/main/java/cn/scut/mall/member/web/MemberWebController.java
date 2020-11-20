package cn.scut.mall.member.web;

import cn.scut.common.utils.R;
import cn.scut.mall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum, Model model){

        Map<String,Object> page = new HashMap<>();
        page.put("page",pageNum);
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders",r);
        return "orderList";
    }
}
