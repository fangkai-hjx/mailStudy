package cn.scut.mall.order.web;

import cn.scut.mall.order.service.OrderService;
import cn.scut.mall.order.vo.OrderConfirmVo;
import cn.scut.mall.order.vo.OrderSubmitVo;
import cn.scut.mall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    /**
     * 去结算确认页
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        System.out.println("toTrade主线程"+Thread.currentThread());
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "orderConfirm";
    }

    /**
     * 提交订单
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){//以表单的方式提交
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
        if(responseVo.getCode() == 0 ){//下单成功，跳转到支付页
            //下单成功来到支付选择页
            model.addAttribute("submitOrderResp",responseVo);
            return "orderPay";
        }else{//下单失败，跳回到toTrade
            String msg = "下单失败";
            switch (responseVo.getCode()){
                case 1 : msg +="订单信息过期，请刷新再次提交"; break;
                case 2 : msg +="订单商品价格发生变化，请确认后再次提交"; break;
                case 3 : msg +="库存锁定失败，商品库存不足"; break;
            }
            System.out.println(msg);
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.mall.com/toTrade";
        }
        //下单成功 来到支付页
        //下单失败，回到订单确认页重新确认订单信息
    }
}
