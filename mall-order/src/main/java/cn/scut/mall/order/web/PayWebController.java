package cn.scut.mall.order.web;

import cn.scut.mall.order.config.AlipayTemplate;
import cn.scut.mall.order.service.OrderService;
import cn.scut.mall.order.vo.PayVo;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;
    @Autowired
    private OrderService orderService;

    /**
     * 1 将支付页让浏览器展示
     * 2 支付成功后，我们要跳到 用户的 订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "payOrder",produces = "text/html")//产生text/html类型的数据
    public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {
//        PayVo payVo = new PayVo();
//        payVo.setBody();//订单的备注
//        payVo.setOut_trade_no();//订单号
//        payVo.setSubject();//订单标题
//        payVo.setTotal_amount();//订单金额
        PayVo payVo = orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);//这里放回的是一个页面，将此页面交给浏览器解析

        return pay;
    }
}
