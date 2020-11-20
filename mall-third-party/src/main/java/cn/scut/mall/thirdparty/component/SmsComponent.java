package cn.scut.mall.thirdparty.component;

import cn.scut.mall.thirdparty.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信组件
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
public class SmsComponent {
    private String host;
    private String path;
    private String appcode;
    private String templateId;

    public void sendSmsCode(String phone,String code){
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("code", code);
        querys.put("phone", phone);
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            HttpResponse response = HttpUtils.doGet(host, path, "GET",headers,querys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
