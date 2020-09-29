package cn.scut.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallThirdPartyApplicationTest {

    @Autowired
    OSSClient ossClient;

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
