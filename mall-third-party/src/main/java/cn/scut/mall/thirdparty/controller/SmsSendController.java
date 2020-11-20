package cn.scut.mall.thirdparty.controller;

import cn.scut.common.utils.R;
import cn.scut.mall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sms")
public class SmsSendController {


    @Autowired
    private SmsComponent smsComponent;
    /**
     * 提供给别的服务调用
     * @return
     */
    @ResponseBody
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone")String phone,@RequestParam("code")String code){
        smsComponent.sendSmsCode(phone,code);//发送验证码
        return R.ok();
    }
}
