package cn.scut.mall.order.listener;

import cn.scut.mall.order.config.AlipayTemplate;
import cn.scut.mall.order.service.OrderService;
import cn.scut.mall.order.vo.PayAsyncVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝支付成功 会不断 的 异步回调该接口
 */
@RestController
public class OrderPayedListener {

    @Autowired
    OrderService orderService;

    @PostMapping("payed/notify")
    public String handleAlipayed(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
        //只要我们收到支付宝给我们的异步通知，告诉我们订单支付成功，放回success
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        for (String s : parameterMap.keySet()) {
//            System.out.println("参数名:"+s+"  参数数值："+request.getParameter(s));
//        }
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean verify_result = AlipaySignature.rsaCheckV1(params, AlipayTemplate.alipay_public_key, AlipayTemplate.charset, AlipayTemplate.sign_type);
        if (verify_result) {
            //TODO  验签 --》是不是支付宝返回的数据？是否被篡改
            System.out.println("签名验证成功！");
            String result = orderService.handlePayResult(vo);
            return result;
        } else {
            System.out.println("签名验证失败！");
            return "error";
        }
    }
}
