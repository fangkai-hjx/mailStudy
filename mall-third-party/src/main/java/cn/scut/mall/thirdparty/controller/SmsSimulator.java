package cn.scut.mall.thirdparty.controller;

import cn.scut.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@Slf4j
public class SmsSimulator {

    /**
     * 这是 个 模拟器
     * 模拟 发送 验证码 给用户手机
     * @param code
     * @return
     */
    @GetMapping("/sms/send")
    public R simulatorMessage(String code,String phone){
//        Random random = new Random();
//        int rannum= (int)(random.nextDouble()*(99999-10000 + 1))+ 10000;
//        String code = (String) params.get("code");
        return R.ok().put("code","手机号是："+phone+"的用户，你的验证码是:"+code);
    }
}
