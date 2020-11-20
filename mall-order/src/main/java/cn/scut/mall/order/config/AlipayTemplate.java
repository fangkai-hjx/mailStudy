package cn.scut.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import cn.scut.mall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016102700768717"
            ;

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkZqUj6oH94SNR6cpIpDc0jDzzOAP7TDia79iduLPpq+MJuEDLnz3xfW3OnV1P8jhjj7oKMvp+FVmdvwGUCBSO9aIcahrnO1RD1FKO7KfZ+xq8MAts7TYOUlJ69PNu3SkyixEPT2rh63ufJ/vwzDm/qQOKI7P2HiNkrxoMgeVH6KMDtay4erO8rF1yyZntA1htdygmaBV1VPGKQ+YAvGKSuPxkWa9Q5R1Aj9ddshDJGv2yDDXu2J27ESJZy7RVemVHf7wN+IfVOAcvahv0xSi69VAMj+OsYK9eG42njug9ZnMqz0kKkvApu9Vq2JwbuBoCAIQnRY0GH29yEPSTzbM7AgMBAAECggEBAIVOzl3/TIUnSFKltueMcU9k4A+AvonJVqUcE5RhL1ItlR5OkAwNBleJk77Uj9PB38Oryfl3OPOAMHjfZ1I38yOxPlC4ITSbQUr3IjLO55S0LwumEb2z4/9c9ZDKi5K8NynK5nx5s3uaTVXTQFIT6Efnrv0W0liiO6Tq775wnhDd3zohacOgBw5IkSRZDGhvKDwNn67EqKvbFM4BsaAeCieh0WLgnT/MwdzEtOHW1iC+NUKtuD7tl0VrNCHEgSKoMstDiiqAkhH+vj+E9waGd2gxtE0xxRCs7vw4GFl81a2pP1mb1N7WA0Zn0JDH8gaQUF44N2MiZYg0uMS4cbWZpYECgYEA4Ej7mbiTyaPfIvmw5JssV3LEz7zsaQq0QpEjQNmmr+H4wyT7F0U+iYOOBfQ252CoDPVM0cksH8+1kZ98ISnXpU6oL6WXmDpXJZlmGzJYsFYpDCq3x8iV6O3A9LKaQbRmsJtdGFRFpYrxtSuDUycGTk5LkzCD5Z+vuj/EPMlHZKECgYEAu6XiW1mmI1MfGw9YvGdRQOsFm8jfjscyn+sxi8HCeSx1dFosQLJRqOD61ar6bMPohUTjkIVWDypJ0hV/uV2r1oKHJpld6uaN1IBBRDuXJtf20ZLKz973Fg2pbzPiB+s/dXhWMJ9H22gPWvKrIAQgoJM50rRxWzKNkfweYhxYLlsCgYEA1DmbAakbBrjuQk0caiS+v3yU6LEuznJXgYmOXyXUSSc82YKbqThXZY27DL06WpQmUCSo7PU7sFo9x66uqT7Lk9sq3/MD0AKcRbKyYFeyfiD5NE8TmZKe70pRw/lgZ33JkaFhZM0RHgSslC65VCxgFiaV8o/8roH8qPL/wDkBOiECgYABpACnpdADDQ3/kZHIC+s08xkYdJgnymwR49+KBkaqAo6AVu1+H1EBdf4bciRAYTIeQCzxLYlEFq2QjamROFVxSbDe0IZ2sE9bJUfszcyThV3eFTd6u8tTQQlTNJPsV801PLkcMbhSHcXMmMSQQhj67m6Z/gPjtTxxo0+ssey/cwKBgEhgzDnMK+mT6ZgFcHlOYFcjtlmkR6TujSVR1ZH/urXZgHZ/X3dpkTfO4yPsRmqADnr34RV50/z1eQ1qEu5o1SJ9q5e2RRiJLIq/BiZ5oHVWhy/R7tcx9MRzNGhIqFVg8vbsKhb9COCzO4I1rb7b95QcbZHzZuGgnyLtqFCGlQ+x";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk+nswym7ZC2mfEyprvHnibSRnHHfcMbcDzrdc6rTG2IFb609Q+eMWAuC2EQoh1PWjgCSpwcBLst3Zx/krOmTWRitZ67x4+N3c7ujJNjPZSMtBl+E/RSdlrnLW9GYevjGi/SfnjkFDpO7I7liYlKJGvdjQDq1CZTnmn7fR+v8emQIJn19pO0pWl4EC1hivXxEoCZIZTYRu2xmixABZ68haDn21Ihb+rlEp5hffAv7ScNPpUV439Sc24yFJwtFdP3UPzKV9eGTGbCvnREa/SmMRDnrjACOx38bUZob3/pmHLhNo0FNlUkTwdsA7IQ7Slm9Yn7KESb6xVIPTX6ChLg4QwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    public String notify_url = "http://fw25bx5pe0.52http.tech/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    public String return_url = "http://member.mall.com/memberOrder.html";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    // 请求网关地址
    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset =  "UTF-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
