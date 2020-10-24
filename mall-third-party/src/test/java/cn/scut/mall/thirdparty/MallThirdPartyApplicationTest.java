package cn.scut.mall.thirdparty;

import cn.scut.mall.thirdparty.component.SmsComponent;
import cn.scut.mall.thirdparty.utils.HttpUtils;
import com.aliyun.oss.OSSClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallThirdPartyApplicationTest {

    @Autowired
    OSSClient ossClient;

    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSendCode(){
        smsComponent.sendSmsCode("16607725735","11111");
    }

    //测试文件上传
    @Test
    public void testUpload() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("D:\\1.jpg");
        ossClient.putObject("fangkai-file", "3.jpg", inputStream);
        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功");
    }

}
