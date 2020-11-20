package cn.scut.mall.seckill.controller;

import cn.scut.common.utils.R;
import cn.scut.mall.seckill.service.SeckillService;
import cn.scut.mall.seckill.to.SeckillSkuCacheTo;
import com.mysql.cj.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;
    /**
     * 查询 当前时间 可以参加 秒杀活动的商品
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        log.info("getCurrentSeckillSkus 方法被调用！");
        List<SeckillSkuCacheTo> list = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(list);
    }

    /**
     * 根据skuId 查询当前商品是否参与秒杀活动
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R skuSeckillInfo(@PathVariable("skuId")Long skuId){
        SeckillSkuCacheTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public String secKill(@RequestParam("killId")String killId,
                          @RequestParam("num")Integer num,
                          @RequestParam("key")String key, Model model){

        String orderSn = seckillService.kill(killId,key,num);
        //1 判断是否登录
        System.out.println("秒杀订单Id:"+orderSn);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }


}
